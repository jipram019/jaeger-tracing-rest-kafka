# Distributed Tracing with Jaeger and OpenTracing

This project simulates tracing of multiple microservices using OpenTracing and Jaeger.

The system collects trace injected into either Kafka headers or HTTP headers and the trace/span will be displayed through Jaeger UI.


[a link] file:///D:/Learnings/jaeger-app.drawio.html


There are 2 branches for this project:
The master branch containing source code for system with single point of failure since all the traces from each app are collected directly into Jaeger endpoint.

app3 (KafkaProducer, KafkaStream) --> Jaeger endpoint

app2 (KafkaProducer, KafkaConsumer, REST HTTP Call) --> Jaeger endpoint

app1 (KafkaConsumer, KafkaProducer) --> Jaeger endpoint

app4 (RESTServices) --> Jaeger endpoint



The collector-app branch containing source code for system with more resilient, fault tolerant span orchestration of which traces from each app are sent into collector app which the collector then consumes span from kafka topic. The collector then send the traces to Jaeger endpoint thorugh HTTP Call.

app3 (KafkaProducer, KafkaStream) --> collector-app (KafkaConsumer) --> Jaeger endpoint

app2 (KafkaProducer, KafkaConsumer, REST HTTP Call) --> collector-app (KafkaConsumer) --> Jaeger endpoint

app1 (KafkaConsumer, KafkaProducer) --> collector-app (KafkaConsumer) --> Jaeger endpoint

app4 (RESTServices) --> collector-app (KafkaConsumer) --> Jaeger endpoint




app3 is entry point service for data collected from input file. app3 will do some cleansing of the data with KStream.
app1 is the sink service which dumps data into ElasticSearch


Both app1 and app3 work with KafkaConnector
