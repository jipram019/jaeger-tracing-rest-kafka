package com.tracing.kafka.app3.config;

import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import java.util.Map;

@Configuration
public class StreamConfig {
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig streamsConfig(KafkaProperties kafkaProperties){
        Map<String,Object> properties = kafkaProperties.buildStreamsProperties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        return new StreamsConfig(properties);
    }
}
