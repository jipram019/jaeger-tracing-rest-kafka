package com.tracing.kafka.collect.collector;

import io.jaegertracing.internal.exceptions.SenderException;
import io.jaegertracing.thriftjava.Batch;
import okhttp3.*;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JaegerClient {
    private static final Logger logger = LoggerFactory.getLogger(JaegerClient.class);
    private static final String JAEGER_HTTP_THRIFT_FORMAT_PARAM = "format=jaeger.thrift";
    private static final MediaType MEDIA_TYPE_THRIFT = MediaType.parse("application/x-thrift");

    private final OkHttpClient okHttpClient;
    private final Request.Builder requestBuilder;
    private final TDeserializer tDeserializer;

    public JaegerClient(String jaegerHost, Integer jaegerPort) throws TTransportException {
        String jaegerEndpoint = String.format("%s:%s/api/traces",jaegerHost,jaegerPort);
        HttpUrl httpUrl = HttpUrl.parse(String.format("%s?%s",jaegerEndpoint, JAEGER_HTTP_THRIFT_FORMAT_PARAM));
        if(httpUrl==null){
            throw new IllegalArgumentException("Could not parse url");
        }

        this.okHttpClient = new OkHttpClient.Builder().build();
        this.requestBuilder = new Request.Builder().url(httpUrl);

        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        this.tDeserializer = new TDeserializer(protocolFactory);
    }

    private Batch deserializePayload(byte[] payload) throws SenderException {
        Batch batch = new Batch();
        try {
            tDeserializer.deserialize(batch,payload);
        } catch (TException e) {
            throw new SenderException("Failed to deserialize message", e, 1);
        }
        return batch;
    }

    public void dumpTrace(byte[] payload) throws SenderException {
        /** Trace will be sent to Jaeger endpoint in the form of Batch **/
        Batch batch = deserializePayload(payload);

        int size = batch.getSpans().size();
        logger.info("Sending {} spans to Jaeger", size);

        /** Sending actual http request to Jaeger **/
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_THRIFT, payload);
        Request request = requestBuilder.post(requestBody).build();

        /** Get the response from Jaeger **/
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
        }
        catch (IOException e) {
            throw new SenderException(String.format("Could not send %d spans to Jaeger", size), e, size);
        }

        /** Successful sending message **/
        if(response.isSuccessful()){
            logger.info("Payload successfully sent");
            response.close();
            return;
        }

        /** Handle unsuccessful response **/
        String responseBody;
        try {
            responseBody = response.body() != null? response.body().string() : "null";
        }
        catch (IOException e) {
            responseBody = "Unable to read response";
        }
        String exceptionMessage = String.format("Could not send %d spans, got response %d: %s from Jaeger",
                size, response.code(), responseBody);
        throw new SenderException(exceptionMessage, null, size);
    }
}
