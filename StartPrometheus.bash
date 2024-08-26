docker stop prometheus
docker rm prometheus
docker run -it -d -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml -p 9090:9090 --name prometheus prom/prometheus
