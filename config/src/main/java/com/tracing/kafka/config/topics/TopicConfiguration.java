package com.tracing.kafka.config.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfiguration {

    @Bean
    public NewTopic numberTopic() {
        return new NewTopic("number.topic", 1, (short) 1);
    }

    @Bean
    public NewTopic oddTopic() {
        return new NewTopic("odd.topic", 1, (short) 1);
    }

    @Bean
    public NewTopic evenTopic() {
        return new NewTopic("even.topic", 1, (short) 1);
    }

    @Bean
    public NewTopic operationTopic() {
        return new NewTopic("operation.topic", 1, (short) 1);
    }

    @Bean
    public NewTopic sumTopic() {
        return new NewTopic("sum.topic", 1, (short) 1);
    }
}
