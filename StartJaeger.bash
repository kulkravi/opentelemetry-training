#!/bin/bash
docker stop jaeger
docker rm jaeger
docker run -d \
  -e COLLECTOR_ZIPKIN_HOST_PORT=:9413 \
  -p 5775:5775/udp \
  -p 6833:6831/udp \
  -p 6834:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14269:14268 \
  -p 14251:14250 \
  -p 9413:9413 \
  --name jaeger \
  jaegertracing/all-in-one:latest
