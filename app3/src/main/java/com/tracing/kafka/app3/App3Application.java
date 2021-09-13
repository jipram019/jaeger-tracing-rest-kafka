package com.tracing.kafka.app3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class App3Application {

	public static void main(String[] args) {
		SpringApplication.run(App3Application.class, args);
	}

}
