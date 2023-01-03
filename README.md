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
---------------------------------------------------
# PATTERN BASED CHANGE

# DETECTION ALGORITHMS

# APPLIED TO CLIMATE DATA

# CRISP-DM

## Computer Science

## A.Y. 2022/

Lorenzo Capocchiano
Domenico Narracci


## Contents

- Business Understanding
    - Business Objectives
    - Business Success Criteria
    - Assess Situation
    - Data Mining Goal
    - Data Mining Success Criteria
    - Project Plan
- Data Understanding
    - Collect Initial Data
    - Data Description Report
        - Verify Data Quality
        - Explore Data
- Data Preparation
    - Select Data
    - Clean Data
    - Construct Data
    - Integrated Data
    - Format Data
- Modelling
- Evaluation


## Business Understanding

### Business Objectives

Retrieve and analyze climate data over last decades to discover patterns and detect how and to what extent
climate has been changed over the time.

The goal of this works is to understand:

1. The period of the year most affected by climate changes
2. Which years are more affected by the change

The reason of this business needs is understanding when and how the climate is changing from the beginning
of 21st century to nowadays.

### Business Success Criteria

Detect Concept Drifts to improve predictions on climate changes data.

### Assess Situation

- **Resources available** :
  o Our PCs with Docker containers
  o Microsoft Teams
- **Data Available** :
  o NOAA dataset
- **Time constraints** :
  o Availability to develop the solution only during nights after workdays and weekends.

### Data Mining Goal

The goal is to mine patterns to be compared and to discover changes between them.

### Data Mining Success Criteria

The data mining success criteria is identified from the discovery of changes with a minimum support of **0.**.

### Project Plan

- Selected technologies:
  o Java 8
  o Docker
  o jKarma
  o Spark
  o GitHub


## Data Understanding

The used dataset was completely delivered from **NOAA** ( _National Oceanic and Atmospheric Administration_ )
website, which is one of the most important datacenters for the environmental information.
As said into the business objectives paragraph, data interval is from 2000 to 2022 of climate detections in
Louisiana and Mississippi, but they can be changed or extended basing on needs.

### Collect Initial Data

As said, the dataset was collected from NOAA website.

### Data Description Report

Dataset structure for the case study is composed by:

- **23** CSV Files
- **9** columns
  o **7** numerical
  o **2** categorical ( _Station and Name_ )

#### Verify Data Quality

- **Data Completeness** : some columns have missing data.
- **Data Consistency** :
  o In some rows, numerical columns have ‘9999’ value.

#### Explore Data

Descriptions of data:

- **STATION –** Identifier
- **NAME -** (max 50 characters) is the name of the station (usually city/airport name).
- **DATE** - Date of the record in the format _‘yyyy-MM-dd’_
- **AWND** - Average daily wind speed _m/s_
- **PRCP** - Precipitation in _mm_
- **SNOW** - Snowfall in _mm_
- **TMAX** - Maximum temperature in Celsius degrees
- **TMIN** - Minimum temperature in Celsius degrees
- **TOBS** - Temperature at the time of observation in Celsius degrees


## Data Preparation

### Select Data

Drop columns that give no useful information:

- **AWND**
- **TOBS** where ‘ _null’_

### Clean Data

- Removed null values
- Sorted values by DATE.

### Construct Data

A new column, named ‘ _period’_ , was constructed to compare rows from the same month in different years.
Period column is a new categorical field which generalize the information of the incoming month for the own
row.

### Integrated Data

Phase skipped because there aren’t other data sources available.

### Format Data

- DATE field formatted for visualization scopes removing timestamp information.


## Modelling

The model identified to achieve objectives is based on jKarma, especially in the application of PBCD algorithm
with a cumulative sliding strategy.
Those characteristics are performed and completed by the realization of a custom climate data joiner and
the applying of an Eclat algorithm with a DFS mining strategy.

To improve the speed of all these technical instruments, we parallelized the execution of the workflow using
Spark.

## Evaluation

Due to its nature, this project has no explicit measure of evaluation and results cannot be compared to a
ground truth.
By the way is still possible to describe findings by analyzing results of the change detection algorithm.

We can notice that changes in the pattern are delimited into the summer season, from July to September as
shown in figures below.

It’s easy to see how data about temperatures are slightly increasing over years.

```
Figure 1 – First 6 months + July
```

```
Figure 2 - August
```
```
Figure 3 - September
```
_Figure 4 – End of the year_


****