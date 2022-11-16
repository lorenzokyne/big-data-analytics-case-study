# big-data-analytics-case-study
Big Data Analytics - Case study - Capocchiano Lorenzo & Narracci Domenico

In the project folder
1. Execute in command bash "docker-compose up --scale spark-worker=[num of workers]"

2. Navigate to <a>http://localhost:8080</a> for the SPARK UI

In the master bash:<br>
3. Launch "./bin/spark-submit --driver-memory 2g --executor-memory 2g --class Application --master spark://bdacs:7077 --conf spark.jars.ivy=/tmp/.ivy --name "BDA Case Study" climate-change.jar "./csv/" spark://bdacs:7077"