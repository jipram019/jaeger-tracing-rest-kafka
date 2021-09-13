package com.tracing.kafka.app3.input;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InputProducer {
    private static final Logger logger = LoggerFactory.getLogger(InputProducer.class);
    private final KafkaTemplate<String,Integer> kafkaTemplate;

    public InputProducer(KafkaTemplate<String, Integer> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${app3.kafka.producer.output-topic}")
    String outputTopic;

    public KeyValue<String,Integer> map(String key, InputValue inputValue){
        if(inputValue==null){
            return new KeyValue<>(null,null);
        }

        int inputNumber;
        try {
            inputNumber = Integer.parseInt(inputValue.getValue());
        } catch (NumberFormatException e) {
            logger.error("Error Parsing Input Number", e);
            return new KeyValue<>(null,null);
        }

        if(isOddNumber(inputNumber)){
            toDeadLetterTopic(key,inputNumber,inputValue.getHeaders());
            return new KeyValue<>(null,null);
        }

        return new KeyValue<>(key, inputNumber);
    }

    private void toDeadLetterTopic(String key, int inputNumber, Headers headers){
        logger.info("Start publishing odd number into {} with value={}",outputTopic, inputNumber);
        ProducerRecord<String,Integer> record = new ProducerRecord<String,Integer>(
                outputTopic,
                null,
                null,
                inputNumber,
                headers
        );
        kafkaTemplate.send(record);
    }

    private boolean isOddNumber(int inputNumber){
        return (inputNumber%2 != 0);
    }
}
