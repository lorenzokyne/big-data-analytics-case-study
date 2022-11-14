#
# Package stage
#
FROM bitnami/spark:3.3.1
ADD /target/climate-change-detector-1.0-SNAPSHOT-jar-with-dependencies.jar climate-change-test.jar
# ENTRYPOINT ["java","-jar","climate-change-test.jar"]
EXPOSE 4040
EXPOSE 7077
EXPOSE 8080-8082
COPY src/main/resources/csv/ csv/
CMD [ "sbin/start-worker.sh", "spark://bdacs:7077" ]
# RUN ./bin/spark-submit --class Application --master spark://bdacs:7077 --conf spark.jars.ivy=/tmp/.ivy --name "BDA Case Study" climate-change-test.jar "./csv/" "spark://bdacs:7077"