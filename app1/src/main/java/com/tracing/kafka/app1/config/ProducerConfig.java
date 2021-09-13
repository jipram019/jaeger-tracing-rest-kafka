package com.tracing.kafka.app1.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ProducerConfig {
    private final Tracer tracer;

    @Value("${app1.kafka.bootstrap-servers}")
    String bootstrapServers;

    public ProducerConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    @Bean
    public TracingKafkaProducer<String,String> kafkaProducer(){
        Properties properties = new Properties();
        properties.setProperty(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);
        return new TracingKafkaProducer<>(producer, tracer);
    }
}
