# Big Data Analytics - Case study 
### Computer Science - Knowledge Engineering and Machine Intelligence
### Authors: Capocchiano Lorenzo & Narracci Domenico<br><br>
## Run Project Guide
Please, follow this quick guide to correctly launch the project

0. Open the bash
1. Execute the command `docker-compose up --scale spark-worker=<num of workers>` to build containers and launch a master node and N workers
2. Navigate to <a>http://localhost:8080</a> for the SPARK UI
3. Open an other bash and use `docker exec -it bdacs bash` to open the master node bash
3. It the master node bash run the following command to send data from CSV to elaborator:
```
./bin/spark-submit --class Application --conf spark.jars.ivy=/tmp/.ivy --name "BDA Case Study" climate-change.jar "./csv/" spark://bdacs:7077
```
4. Enjoy watch what happens on the UI :)