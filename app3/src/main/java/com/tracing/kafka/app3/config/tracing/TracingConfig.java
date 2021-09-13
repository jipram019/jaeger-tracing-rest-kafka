package com.tracing.kafka.app3.config.tracing;

import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.streams.TracingKafkaClientSupplier;
import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import javax.annotation.PostConstruct;

@Configuration
public class TracingConfig {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${bootstrap.server}")
    String bootstrapServer;

    @Value("${tracing.topic}")
    String tracingTopic;

    private final StreamsBuilderFactoryBean streamsBuilderFactory;

    public TracingConfig(StreamsBuilderFactoryBean streamsBuilderFactory) {
        this.streamsBuilderFactory = streamsBuilderFactory;
    }

    @Bean
    public Tracer tracer(){
        return io.jaegertracing.Configuration.fromEnv(appName)
                .withSampler(
                        io.jaegertracing.Configuration.SamplerConfiguration.fromEnv()
                                .withType(ConstSampler.TYPE)
                                .withParam(1))
                .withReporter(
                        io.jaegertracing.Configuration.ReporterConfiguration.fromEnv()
                                .withFlushInterval(1000)
                                .withLogSpans(true)
                                .withMaxQueueSize(10000)
                                .withSender(
                                        new SenderConfig(bootstrapServer, tracingTopic)
                                ))
                .getTracer();
    }

    @PostConstruct
    public void registerToGlobalTracer() {
        GlobalTracer.registerIfAbsent(tracer());
    }

    @PostConstruct
    public void setClientSupplierForStreams() {
        streamsBuilderFactory.setClientSupplier(new TracingKafkaClientSupplier(tracer()));
    }
}