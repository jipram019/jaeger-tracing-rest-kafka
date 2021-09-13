package com.tracing.kafka.app2.config;

import io.opentracing.contrib.kafka.TracingConsumerInterceptor;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Collections;
import java.util.Map;

@Configuration
public class ConsumerConfig {
    private final KafkaProperties kafkaProperties;

    public ConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConsumerFactory<?,?> kafkaConsumerFactory(){
        Map<?,?> properties = kafkaProperties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Integer> numberContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Integer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setConsumerFactory(numberConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String,Integer> numberConsumerFactory(){
        Map<String,Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, Collections.singletonList(TracingConsumerInterceptor.class));
        return new DefaultKafkaConsumerFactory<>(properties);
    }
}
