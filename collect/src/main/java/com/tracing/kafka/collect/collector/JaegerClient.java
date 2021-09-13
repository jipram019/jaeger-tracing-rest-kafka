package com.tracing.kafka.collect.collector;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.http.client.HttpClient;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class JaegerClient {
    private static final Logger logger = LoggerFactory.getLogger(JaegerClient.class);
    private static final String JAEGER_HTTP_THRIFT_FORMAT_PARAM = "format=jaeger.thrift";

    private final OkHttpClient okHttpClient;
    private final Request.Builder requestBuilder;
    private final TProtocolFactory protocolFactory;

    public JaegerClient(String jaegerHost, Integer jaegerPort)  {
        String jaegerEndpoint = String.format("%s:%s/api/traces",jaegerHost,jaegerPort);
        HttpUrl httpUrl = HttpUrl.parse(String.format("%s?%s",jaegerEndpoint, JAEGER_HTTP_THRIFT_FORMAT_PARAM));
        if(httpUrl==null){
            throw new IllegalArgumentException("Could not parse url");
        }

        this.protocolFactory = new TBinaryProtocol.Factory();
        this.okHttpClient = new OkHttpClient.Builder().build();
        this.requestBuilder = new Request.Builder().url(httpUrl);
    }

    public void dumpTrace(byte[] payload) {
        try {
            TDeserializer tDeserializer = new TDeserializer(protocolFactory);
        } catch (TTransportException e) {
            logger.error("Could not initialize Thrift deserializer", e);
            return;
        }
    }
}
