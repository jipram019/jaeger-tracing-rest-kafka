package com.tracing.kafka.app1.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Properties;

@Configuration
public class ConsumerConfig {
    private final Tracer tracer;

    @Value("${app1.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${app1.kafka.consumer.max-poll-records}")
    String maxPollRecords;

    @Value("${app1.kafka.consumer.enable-auto-commit}")
    String enableAutoCommit;

    @Value("${app1.kafka.consumer.auto-offset-reset}")
    String autoOffsetReset;

    @Value("${app1.kafka.consumer.group-id}")
    String groupId;

    @Value("${app1.kafka.consumer.client-id}")
    String clientId;

    @Value("${app1.kafka.input-topic}")
    String inputTopic;

    public ConsumerConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    @Bean
    public TracingKafkaConsumer<String,String> kafkaConsumer(){
        Properties properties = new Properties();
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId);

        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singletonList(inputTopic));
        return new TracingKafkaConsumer<>(consumer, tracer);
    }
}
