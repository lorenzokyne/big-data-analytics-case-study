# Big Data Analytics - Case study 
### Computer Science - Knowledge Engineering and Machine Intelligence
### Authors: Capocchiano Lorenzo & Narracci Domenico<br><br>
## Run Project Guide
Please, follow this quick guide to correctly launch the project

*Pre-requisites:
You may need jkarma library as it is not available on standard mvn repository  <br>
Latest version is downloadable from Official BitBucker Repo: <a>https://bitbucket.org/jkarma/jkarma/downloads/</a>*

After downloaded jKarma, you can install it by running the following command
```
mvn install:install-file -Dfile="src/main/resources/jkarma-1.0.0.jar" -DgroupId="org.jkarma" -DartifactId="jkarma-core" -Dversion="4.0.0" -Dpackaging=jar
```
---------------------------------------------------
1. Open a bash into the java project folder
2. Run `mvn clean install` to build the .jar file
3. Execute the command `docker-compose up --scale spark-worker=<num of workers>` to build containers and launch a master node and N workers
4. Navigate to <a>http://localhost:8080</a> for the SPARK UI
5. Open an other bash and use `docker exec -it bdacs bash` to open the master node bash
6. It the master node bash run the following command to send data from CSV to elaborator:
```
./bin/spark-submit --class Application --conf spark.jars.ivy=/tmp/.ivy --conf spark.executor.cores=1 --conf spark.driver.cores=3 --conf spark.cores.max=3 --name "BDA Case Study" climate-change.jar "./csv/" spark://bdacs:7077
```
7. Enjoy, watch what happens on the UI :)