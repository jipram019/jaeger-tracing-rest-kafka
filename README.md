# Distributed Tracing with Jaeger and OpenTracing

This project simulates tracing of multiple microservices using OpenTracing and Jaeger.

The system collects trace injected into either Kafka headers or HTTP headers and the trace/span will be displayed through Jaeger UI.

https://viewer.diagrams.net/?tags={}&highlight=0000ff&edit=_blank&layers=1&nav=1&title=jaeger-app.drawio#R7Vxde6I4FP41XspDCCBcdhyd3dnOtM%2F0mdndS4RUmUFhQmy1v34DBIQkFLRI6VovWj2BEzjnzXs%2Bgo7gdL37hJ1o9SX0UDDSVG83gh9HmjaxNPo3EewzATA1K5Msse8x2UFw5z8hJlSZdOt7KK4cSMIwIH5UFbrhZoNcUpE5GIeP1cPuw6A6a%2BQskSC4c50glyrGQf6375EVkwPTPgz8gfzlik1uaWY2sHDcX0scbjdsxk24QdnI2snVsLuMV44XPpZEcDaCUxyGJHu33k1RkBg2t1l%2BHtnnFzqCH1ZkHdAPgL5Nh%2Bc1J4M2J9P7wmhDytPV6Xt6%2Bv3z9vEv8uPLdvztaffnj6vdbMwc%2F%2BAEWzbJVRSJM6f2QYkilc78uPIJuoscNxl9pIiqXti9HwTTMAhxei6c0td8Lr1gNtEDwgTtSiJ2A59QuEYE7%2BkhbFSDzKQMqGNgM8Fj2elMtir522AyhyFtWeg%2BmIy%2BYVY7woJQZkFtsBaEk8FZUJdZEA7WggYYnAUNmQX1Ti04MzrEoM1ZUGLASZ%2F2MwX7CbZDG%2B8qCVT0kxs4cey7TWzMzIK8SugSjVK6aeOZm8YocIj%2FUA2DMkuwGW5Dn15JYXNdQK3OWTMOt9hF7LRyHGnSZHOKiIOXiAiKUs8U9326s6wLcJbeYOLWvuIVCV6vcRY1n7MvHRYlB8RHXDGAlWyEvslUdgoFWwIFMyAJe4Xp1R0wYf7ehvnAOE4T2Ct6AFCj3WGQvlsm%2Fzfb9QJhhYQRRQ7TSC8wU5od8gy5gmZyDZwFCj4UmWfOs2nuKVCv5yDrPkFwTHD4C5VGTNdCi%2FtuSBnwiYEmYWVLgnntXKyc80zn7kUPaHNZzoXa4JwLzuTc0PMuy7eablR9Wzj71Xwr5qNHh2jPiVeFU4YerzU%2B%2BhXp67EBW9AEes6uQAe5MPUS3v%2BTlBkK0LVc8G8yqqi6ngs%2B7lglkn3alz%2FdIuzTW0GYCaulSztwZJZqEWAGgiJY29w4FkXQ4vHYMu3rDEUdJOlviwEsXTEs3n3wRBKQKINqzx6U5db%2FZw8KTYmTOVzQ1DeHa%2BdKnMMIYWrj8MKyZ91okT3bfWZYmix7fptReiDLX6%2Ftix%2Fdc5kABZocYExbAcA%2BvPrlA%2B3CuNwAtmLbgrlfzOwNenvneXHvqRuej7fry2J4Y3DNL03cFXtn%2BBeRgqnaChQ5uLR4Fcs6jRdMoCqmpRYvILK%2FhDb6YonmLnn%2BPEfOA0CkhPyURRtWseqb6rFCdqREKwt%2BgiaqiVdOhFK6IKiZaw5Pd9xsSeAnDMPCGf51Q8%2FySQp3RTWOJab5fDqdmQkxZdajZu6Gijgmgnk3rbRKTMkq4WNadw84yLKHV8DPjFIc8d0YOdhddYEgd08B4SEMW8Aoo73rRROumCnA6XDqAEITfof29TEky1SOjGaDjy9ClWidmGYK%2FhM0nTlkQFny0UViSTBdYZeVWkIOFkBMLIEMlWfLLGHXuzOlNFO1iqwySTPHNPE0J6fkmbWGb2zq59gdCCukvVwu0zz1yRseSrBnVri0pr5QFZ7%2BzJStVTRN%2BnXcpfXydU1YdCd7TlAF%2Bl11%2BYWfg64rPQHK3hfO1cK26elM3XPCpnewH%2FC2Vnh31DyR52d9eU6srmkiGiRfkqnPhIf1FQJLktH2%2BwWCDqrLt4V%2BISjxqeDp4a1v5np5qWnLKs30clnLCedCIIqgKDJEkdK2WqVLiFSBVS0uJZUoEzmBv9wk6KSYS0Lrh2RB%2Bq4TXLGBte95QV3Ls8oNz5fBHVCAZvLPHEpIQJcsgLNVtbqsqj0XjjRRpIsi8xkcvUOrNrrwT0AWgleDlqzu7QBadwT7m6UUXfRW6GGqhJz0YlBCU2YxKGG6d6BxT%2FXwKWTRXnk1oL38%2ByxSoH2%2Bu%2FlKx%2BrwNpokiFEUJUVPfnIFhWo6Zwq5Qnd6xuRjW30V4DJ9tqCPjrRXWYE7Uzlpq%2FJ9OXD7jgYf0iXLQZrXn2095BfQF%2FG2ywXfqbUFtXJYmoj7j70yq9FBhXjom40qXTNj9HzPrNaeje0xFg4GUnSCxl3JtkUnMPnI2%2FNOhnGmorOOWN7rzo6IBfAd2lfP2YwO9lZqDTOQlS%2FZA%2Bmu3wTO13C6%2FfZzf%2F19NZ9eR9%2B3t%2BOv4GE%2BG4ud8s8OWlLkC14LAj%2BK64Bfcp8TR9lvGt37u2QpNDzaI6Bd4vr66tiqsV7Dc6q8kVvgn348%2FLRRZv3Dj0fB2X8%3D


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
