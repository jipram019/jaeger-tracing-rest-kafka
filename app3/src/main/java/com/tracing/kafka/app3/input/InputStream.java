package com.tracing.kafka.app3.input;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

@Configuration
public class InputStream {
    private static final Logger logger = LoggerFactory.getLogger(InputStream.class);
    private static final String TRACING_COMPONENT_NAME = "app3.kafka";
    private static final String TRACING_SERVICE_NAME = "kafka";
    private static final String TRACING_NUMBER_VALUE = "number.value";

    @Value("${app3.kafka.stream.input-topic}")
    String inputTopic;

    @Value("${app3.kafka.stream.output-topic}")
    String outputTopic;

    private final InputProducer inputProducer;

    public InputStream(InputProducer inputProducer) {
        this.inputProducer = inputProducer;
    }

    @Bean
    public KStream<String,Integer> stream(StreamsBuilder streamsBuilder){
        KStream<String,Integer> stream = streamsBuilder
                .stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key,value) -> logger.info("Received message from {} with value={}", inputTopic, value))
                .transform(tracingInputStream())
                .map(inputProducer::map)
                .peek((key,value) -> logger.info("Filtering only event numbers"))
                .filter((key,value) -> value != null);

        /* Send even numbers into even topic */
        logger.info("Start publishing even numbers into {}", outputTopic);
        stream.to(outputTopic, Produced.with(Serdes.String(), Serdes.Integer()));
        return stream;
    }

    private TransformerSupplier<String,String, KeyValue<String, InputValue>> tracingInputStream(){
        return new TransformerSupplier<String,String,KeyValue<String, InputValue>>(){
            public Transformer<String,String,KeyValue<String, InputValue>> get(){
                return new Transformer<String, String, KeyValue<String, InputValue>>() {

                    private ProcessorContext context;

                    @Override
                    public void init(ProcessorContext processorContext) {
                        this.context = processorContext;
                    }

                    @Override
                    public KeyValue<String, InputValue> transform(String s, String s2) {
                        if(s2 == null || s2.isEmpty()){
                            return KeyValue.pair(s, null);
                        }

                        // This is due to first consumer does not have any parent context to inject the span from
                        injectFirstKafkaConsumerSpan(s2, context);
                        return KeyValue.pair(s, new InputValue(s2, context.headers()));
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        };
    }

    private void injectFirstKafkaConsumerSpan(String number, ProcessorContext context){
        Tracer tracer = GlobalTracer.get();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan("Received_").withTag(
                Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER);
        Span span = spanBuilder.start();

        Tags.COMPONENT.set(span, TRACING_COMPONENT_NAME);
        Tags.PEER_SERVICE.set(span, TRACING_SERVICE_NAME);

        span.setTag("partition", context.partition());
        span.setTag("topic", context.topic());
        span.setTag("offset", context.offset());

        /* Add the number or record as span tag */
        span.setTag(TRACING_NUMBER_VALUE, number);
        span.finish();

        TextMap headersMapInjectAdapter = new TextMap() {
            @Override
            public Iterator<Map.Entry<String, String>> iterator() {
                throw new UnsupportedOperationException("iterator should never be used with Tracer.inject()");
            }
            @Override
            public void put(String s, String s1) {
                context.headers().add(s, s1.getBytes(StandardCharsets.UTF_8));
            }
        };
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, headersMapInjectAdapter);
    }
}
