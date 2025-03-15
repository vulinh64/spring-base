@echo off
docker compose down
docker rmi --force spring-base
docker compose up --detach