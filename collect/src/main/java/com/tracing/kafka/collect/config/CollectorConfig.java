package com.tracing.kafka.collect.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

import java.util.Map;

@Configuration
public class CollectorConfig {
    private final KafkaProperties kafkaProperties;

    public CollectorConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConsumerFactory<?,?> kafkaConsumerFactory(){
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConsumerFactory<String, byte[]> collectorConsumerFactory(){
        Map<String,Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> collectorContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, byte[]> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        containerFactory.setErrorHandler(new SeekToCurrentErrorHandler());
        containerFactory.setConsumerFactory(collectorConsumerFactory());
        return containerFactory;
    }
    
}
