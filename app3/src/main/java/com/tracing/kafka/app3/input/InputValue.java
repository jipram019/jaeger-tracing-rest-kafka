package com.tracing.kafka.app3.input;

import org.apache.kafka.common.header.Headers;

public class InputValue {
    private final String value;
    private final Headers headers;

    public InputValue(String value, Headers headers) {
        this.value = value;
        this.headers = headers;
    }

    public String getValue() {
        return value;
    }

    public Headers getHeaders() {
        return headers;
    }
}
