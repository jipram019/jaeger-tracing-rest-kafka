# jaeger-tracing-rest-kafka

This project will simulate tracing of multiple microservices using OpenTracing and Jaeger. The system collects trace injected into either Kafka headers or HTTP headers and the trace/span will be displayed through Jaeger UI.
app3 (KafkaProducer, KafkaStream) --> Jaeger endpoint
app2 (KafkaProducer, KafkaConsumer, REST HTTP Call) --> Jaeger endpoint
app1 (KafkaConsumer, KafkaProducer) --> Jaeger endpoint
app4 (RESTServices) --> Jaeger endpoint

app3 is entry point service for data collected from input file
app1 is the sink service which dumps data into ElasticSearch
app3 will do some cleansing of the data with KStream.

Both app1 and app3 work with KafkaConnector
