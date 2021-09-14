# Distributed Tracing with Jaeger and OpenTracing

This project simulates tracing of multiple microservices using OpenTracing and Jaeger.

The system collects trace injected into either Kafka headers or HTTP headers and the trace/span will be displayed through Jaeger UI.


<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=5,IE=9" ><![endif]-->
<!DOCTYPE html>
<html>
<head>
<title>jaeger-app</title>
<meta charset="utf-8"/>
</head>
<body><div class="mxgraph" style="max-width:100%;border:1px solid transparent;" data-mxgraph="{&quot;highlight&quot;:&quot;#0000ff&quot;,&quot;nav&quot;:true,&quot;resize&quot;:true,&quot;toolbar&quot;:&quot;zoom layers tags lightbox&quot;,&quot;edit&quot;:&quot;_blank&quot;,&quot;xml&quot;:&quot;&lt;mxfile host=\&quot;app.diagrams.net\&quot; modified=\&quot;2021-09-14T12:57:58.953Z\&quot; agent=\&quot;5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36\&quot; etag=\&quot;hIyidl2e3Zw5hiZ8plf-\&quot; version=\&quot;15.2.5\&quot; type=\&quot;device\&quot;&gt;&lt;diagram name=\&quot;Page-1\&quot; id=\&quot;929967ad-93f9-6ef4-fab6-5d389245f69c\&quot;&gt;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&lt;/diagram&gt;&lt;/mxfile&gt;&quot;}"></div>
<script type="text/javascript" src="https://viewer.diagrams.net/js/viewer-static.min.js"></script>
</body>
</html>


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
