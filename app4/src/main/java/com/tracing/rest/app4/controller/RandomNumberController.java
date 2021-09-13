package com.tracing.rest.app4.controller;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class RandomNumberController {
    private static final Logger logger = LoggerFactory.getLogger(RandomNumberController.class);
    private static final String RANDOM_NUMBER_TAG = "random.number";

    @PostMapping(path="/random", produces={"application/json"})
    public ResponseEntity<String> example(){
        int randomNumber = new Random().nextInt(10);
        logger.info("Generated {} was {}", RANDOM_NUMBER_TAG, randomNumber);

        Tracer tracer = GlobalTracer.get();
        Span span = tracer.activeSpan();
        span.setTag(RANDOM_NUMBER_TAG, randomNumber);

        HttpHeaders responseHeaders = new HttpHeaders();
        tracer.inject(
                span.context(),
                Format.Builtin.HTTP_HEADERS,
                new HttpHeadersCarrier(responseHeaders));

        return new ResponseEntity<>(Integer.toString(randomNumber), responseHeaders, HttpStatus.OK);
    }

}
