@echo off
docker run -d --name standalone-elasticsearch -e "xpack.security.enabled=false" -e "discovery.type=single-node" -p 9200:9200 -p 9300:9300 elasticsearch:8.15.2