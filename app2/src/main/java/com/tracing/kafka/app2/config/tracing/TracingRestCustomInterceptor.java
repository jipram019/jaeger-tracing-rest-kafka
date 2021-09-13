package com.tracing.kafka.app2.config.tracing;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.contrib.spring.web.client.RestTemplateSpanDecorator;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class TracingRestCustomInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TracingRestCustomInterceptor.class);
    private static final RestTemplateSpanDecorator REST_TEMPLATE_SPAN_DECORATOR = new RestTemplateSpanDecorator.StandardTags();

    private Tracer tracer;
    private Headers headers;

    public TracingRestCustomInterceptor(Headers headers) {
        this.tracer = GlobalTracer.get();
        this.headers = headers;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        ClientHttpResponse httpResponse;

        SpanContext parentContext = null;
        if(headers != null && headers.toArray().length > 0){
            parentContext = TracingKafkaUtils.extractSpanContext(headers,tracer);
        }

        Span span = tracer
                .buildSpan(httpRequest.getMethod().toString())
                .asChildOf(parentContext)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();

        try (Scope scope = tracer.activateSpan(span)) {
            tracer.inject(
                    span.context(),
                    Format.Builtin.HTTP_HEADERS,
                    new HttpHeadersCarrier(httpRequest.getHeaders()));
        } catch (Exception e) {
            logger.error("Error activating current span", e);
        } finally {
            span.finish(); // Optionally close the Span.
        }

        try {
            REST_TEMPLATE_SPAN_DECORATOR.onRequest(httpRequest, span);
        } catch (RuntimeException e) {
            logger.error("Exception decorating span during request", e);
        }

        try {
            httpResponse = clientHttpRequestExecution.execute(httpRequest, bytes);
        } catch (Exception e) {
            try {
                REST_TEMPLATE_SPAN_DECORATOR.onError(httpRequest, e, span);
            } catch (RuntimeException ex) {
                logger.error("Error decorating span during execution", ex);
            }
            throw e;
        }

        try {
            REST_TEMPLATE_SPAN_DECORATOR.onResponse(httpRequest, httpResponse, span);
        } catch (RuntimeException e) {
            logger.error("Exception decorating span during response", e);
        }

        return httpResponse;
    }
}
