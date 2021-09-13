package com.tracing.kafka.collect.collector;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

public class TracingCollector {
    private static final Logger logger = LoggerFactory.getLogger(TracingCollector.class);
    private static final int SLEEP_DURATION_IN_SECONDS = 20;

    private final JaegerClient jaegerClient;

    public TracingCollector(String jaegerHost, int jaegerPort) {
        jaegerClient = new JaegerClient(jaegerHost,jaegerPort);
    }

    @KafkaListener(topics = "${collect.input-topic}", containerFactory = "collectorContainerFactory")
    public void collectTrace(ConsumerRecord<String, byte[]> record, Acknowledgment ack){
        /** send message to Jaeger **/
        dumpTrace(record);

        /** acknowledge message **/
        ack.acknowledge();
    }

    private void dumpTrace(ConsumerRecord<String,byte[]> record){
        if(record==null || record.value()==null){
            return;
        }

        try {
            // To implement
            jaegerClient.send(record.value());
        } catch (Exception e) {
            String message = String.format("Unable to send spans to Jaeger, retrying offset %s", record.offset());
            logger.error("{}, but first sleep for {}", message, SLEEP_DURATION_IN_SECONDS);
            try {
                Thread.sleep(SLEEP_DURATION_IN_SECONDS * 1000);
            } catch (InterruptedException ex) {
                logger.error("Could not sleep", ex);
            }
            throw new IllegalStateException(message, e);
        }
    }
}
