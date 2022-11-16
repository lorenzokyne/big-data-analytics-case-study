FROM bitnami/spark:latest
ADD /target/climate-change-detector-1.0-SNAPSHOT-jar-with-dependencies.jar climate-change-test.jar
EXPOSE 7077 8080
COPY src/main/resources/csv/ csv/