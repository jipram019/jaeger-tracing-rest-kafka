package com.tracing.kafka.app2.processor;

import com.tracing.kafka.app2.config.TracingRestCustomInterceptor;
import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class OutputProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OutputProcessor.class);
    private static final String TRACING_SPAN_CONTEXT_KEY = "uber-trace-id";
    private static final String TRACING_SECOND_SPAN_CONTEXT_KEY = "second_uber-trace-id";

    private final KafkaTemplate<String,String> kafkaTemplate;

    public OutputProcessor(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${app2.kafka.input-topic}")
    String inputTopic;

    @Value("${app2.kafka.output-topic}")
    String outputTopic;

    @Value("${app2.rest.app-url}")
    String restAppUrl;

    @Value("${app2.rest.app-api}")
    String restAppApi;

    @KafkaListener(topics = "${app2.kafka.input-topic}", containerFactory = "numberContainerFactory")
    public void listenEvents(ConsumerRecord<String,Integer> record, Acknowledgment ack){
        if(record.value()==null){
            return;
        }
        logger.info("Received payload from {} with value={}", inputTopic, record.value());
        int number = record.value();

        /* Get a random number from Rest App4 */
        final String url = restAppUrl + restAppApi;
        HttpEntity<String> response = sendToRestApp(url, record.headers());
        String responseBody = response.getBody();
        logger.info("Received response {} from {}", responseBody, url);

        /* Parse the response into second integer */
        int randomNumber;
        try {
            randomNumber = Integer.parseInt(responseBody);
        } catch (NumberFormatException e) {
            logger.error("Error parsing response from rest app", e);
            return;
        }

        /* Update tracing headers */
        updateTracingHeaders(response.getHeaders(), record.headers());
                
        /* Produce resulting ops and publish to output topic */
        publishOperation(number, randomNumber, record.headers());

        /* Acknowledge the message */
        ack.acknowledge();
    }

    private HttpEntity<String> sendToRestApp(String url, Headers headers){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new TracingRestCustomInterceptor(headers)));
        return restTemplate.exchange(url, HttpMethod.POST,null,String.class);
    }

    private void publishOperation(int firstNumber, int secondNumber, Headers headers){
        String operation = firstNumber + " + " + secondNumber;
        ProducerRecord<String,String> record = new ProducerRecord<String,String>(
                outputTopic,null, null, operation, headers
        );

        logger.info("Start publishing payload into {} with value={}", outputTopic, operation);
        kafkaTemplate.send(record);
    }
    
    private void updateTracingHeaders(HttpHeaders httpHeaders, Headers kafkaHeaders){
        String spanContext = getSpanContextFromHttpHeaders(httpHeaders);
        if(!spanContext.isEmpty() || spanContext != null){
            kafkaHeaders.remove(TRACING_SPAN_CONTEXT_KEY);
            kafkaHeaders.remove(TRACING_SECOND_SPAN_CONTEXT_KEY);
            kafkaHeaders.add(TRACING_SPAN_CONTEXT_KEY, spanContext.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String getSpanContextFromHttpHeaders(HttpHeaders headers){
        List<String> spanContextFromHeaders = headers.get(TRACING_SPAN_CONTEXT_KEY);
        if(spanContextFromHeaders == null || spanContextFromHeaders.isEmpty()){
            return null;
        }

        try {
            return URLDecoder.decode(
                    String.join("",spanContextFromHeaders),
                    StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception reading span context from HttpHeader", e);
            return  null;
        }
    }
}
