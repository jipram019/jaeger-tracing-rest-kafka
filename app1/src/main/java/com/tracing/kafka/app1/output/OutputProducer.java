package com.tracing.kafka.app1.output;

import com.google.gson.JsonObject;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OutputProducer {
    private static final Logger logger = LoggerFactory.getLogger(OutputProducer.class);

    private final TracingKafkaConsumer<String,String> kafkaConsumer;
    private final TracingKafkaProducer<String,String> kafkaProducer;

    public OutputProducer(TracingKafkaConsumer<String, String> kafkaConsumer, TracingKafkaProducer<String, String> kafkaProducer) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
    }

    @Value("${app1.kafka.output-topic}")
    private String outputTopic;

    @Value("${app1.kafka.input-topic}")
    private String inputTopic;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent(){
        listenToEvents();
    }

    private void listenToEvents(){
        boolean forever = true;
        while(forever){
            ConsumerRecords<String,String> records = kafkaConsumer.poll(100L);
            int recordsCount = records.count();
            for(ConsumerRecord<String,String> record : records){
                String payload = record.value();
                logger.info("Received payload from {} with value={}", inputTopic, payload);

                /* Publish to output topic */
                produceOutputPayload(payload, record.headers());
            }

            /* Commit the offset */
            if(recordsCount > 0){
                kafkaConsumer.commitSync();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("Interrupted exception", e);
                }
            }
        }
        /* Close the producer */
        kafkaProducer.close();
    }

    private void produceOutputPayload(String payload, Headers headers){
        int firstNumber, secondNumber;
        String[] payloadArr = payload.split(" \\+ ", 2);

        /* This method will simply compute the sum of two numbers */
        try {
            firstNumber = Integer.parseInt(payloadArr[0]);
            secondNumber = Integer.parseInt(payloadArr[1]);
        } catch (NumberFormatException e) {
            logger.error("Number parsing error:", e);
            return;
        }

        int outputNumber = firstNumber + secondNumber;
        String outputStr = firstNumber + " + " + secondNumber + " = " + outputNumber;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("firstNumber", firstNumber);
        jsonObject.addProperty("secondNumber", secondNumber);
        jsonObject.addProperty("finalNumber", outputNumber);
        jsonObject.addProperty("inputCalculation", payload);
        jsonObject.addProperty("finalCalculation", outputStr);
        String jsonString = jsonObject.toString();

        ProducerRecord<String,String> jsonOutput = new ProducerRecord<String,String>(
                outputTopic,null,null, jsonString, headers
        );

        logger.info("Start publishing into {} with value={}", outputTopic, jsonString);
        kafkaProducer.send(jsonOutput, (RecordMetadata record, Exception exception) -> {
            if(exception != null){
                logger.error("Error publishing into topic {} : {}", outputTopic, exception);
            }
        });
    }
}
