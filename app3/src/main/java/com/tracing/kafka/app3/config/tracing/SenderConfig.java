package com.tracing.kafka.app3.config.tracing;

import io.jaegertracing.Configuration;
import io.jaegertracing.spi.Sender;
import org.apache.thrift.transport.TTransportException;

public class SenderConfig extends Configuration.SenderConfiguration {
    private final String bootstrapServers;
    private final String tracingTopic;

    public SenderConfig(String bootstrapServers, String tracingTopic) {
        super();
        this.bootstrapServers = bootstrapServers;
        this.tracingTopic = tracingTopic;
    }

    @Override
    public Sender getSender(){
        KafkaSender kafkaSender = null;
        try {
           kafkaSender = new KafkaSender(bootstrapServers,tracingTopic);
        } catch (TTransportException e) {
        }
        return kafkaSender;
    }
}
