package com.tracing.kafka.app2.config;

import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class TracingConfig {
    @Value("${jaeger.tracer.host}")
    private String jaegerHost;

    @Value("${jaeger.tracer.port}")
    private Integer jaegerPort;

    @Value("${spring.application.name}")
    private String appName;

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
                                        io.jaegertracing.Configuration.SenderConfiguration.fromEnv()
                                                .withAgentHost(jaegerHost)
                                                .withAgentPort(jaegerPort)
                                ))
                .getTracer();
    }

    @PostConstruct
    public void registerToGlobalTracer(){
        GlobalTracer.registerIfAbsent(tracer());
    }
}
