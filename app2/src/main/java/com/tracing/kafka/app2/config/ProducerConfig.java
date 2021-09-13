package com.tracing.kafka.app2.config;

import io.opentracing.contrib.kafka.TracingProducerInterceptor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Collections;
import java.util.Map;

@Configuration
public class ProducerConfig {
    private final KafkaProperties kafkaProperties;

    public ProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate<String,String>(producerFactory());
    }

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        Map<String,Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, Collections.singletonList(TracingProducerInterceptor.class));
        return new DefaultKafkaProducerFactory<>(properties);
    }
}
