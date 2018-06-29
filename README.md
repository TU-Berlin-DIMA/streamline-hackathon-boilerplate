# Streamline Hackathon Boilerplate for GDELT 1.0 Event Database

This repository contains boilerplate Java/Scala code for Apache Flink and Apache Spark which parse and stream GDELT 1.0 Event Database [1]. It further includes simple aggregation examples on the data.

## Run The Boilerplate (Option 1)

You may run the code from your favorite IDE. You just need to select a class with a static main method as entry point.
Regardless of the usage of Flink or Spark, the selected processing engine will be launched as a internal component.
This approach is recommended for developing and testing purpose.

## Run The Boilerplate (Option 2)

You are supposed to deploy the job on a local Flink/Spark cluster launched on your machine. In order to do so, you first need
to compile your code by executing on the root directory of this repository:
```
mvn clean package
```
### Apache Flink
After than that, you need to submit the job to Flink Job Manager.
Please, be sure that a standalone (or cluster) version of Flink is running on your machine as explained here [2].
Briefly, you need to start Flink by executing:
```
/path/to/flink/root/bin/start-local.sh  # Start Flink
```
Then you can run those long-running jobs.
```
# Java Job
/path/to/flink/root/bin/flink run \
hackathon-flink-java/target/hackathon-flink-java-0.1-SNAPSHOT.jar \
--path /path/to/data/180-days.csv --country USA

# Scala Job
/path/to/flink/root/bin/flink run \
hackathon-flink-scala/target/hackathon-flink-scala-0.1-SNAPSHOT.jar \
--path /path/to/data/180-days.csv --country USA
```
Please, note that those jobs will run forever. In order to shutdown the execution, you need to prompt
```
/path/to/flink/root/bin/flink cancel <jobID>
```
as explained here [3]

### Apache Spark
After that, you need to submit the job to the Spark Cluster.
First you need to start Spark in standalone mode on your local machine as explained here [4].
A quick way to start Spark in standalone mode is to run the following command:
```
/path/to/spark/root/sbin/start-all.sh # Start Spark
```
To run a jar file, you need the Spark master URL which you can find on master's web UI 
(by default [http://localhost:8080]( http://localhost:8080)).

Then you can run those long-running jobs.
```
# Java Job
/path/to/spark/root/bin/spark-submit \
--master spark://<host>:<port> \
--class eu.streamline.hackathon.spark.job.SparkJavaJob \
 hackathon-spark-java/target/hackathon-spark-java-0.1-SNAPSHOT.jar \
--path /path/to/data/180-days.csv \
--micro-batch-duration 5000
--country USA

# Scala Job
/path/to/spark/root/bin/spark-submit \
--master spark://<host>:<port> \
--class eu.streamline.hackathon.spark.scala.job.SparkScalaJob \
hackathon-spark-scala/target/hackathon-spark-scala-0.1-SNAPSHOT.jar \
--path /path/to/data/180-days.csv \
--micro-batch-duration 5000
--country USA

```

To suppress the logs in when the Spark program is running simply rename the 
```
/path/to/spark/root/conf/log4j.properties.template 
to 
/path/to/spark/root/conf/log4j.properties
```
And change the line:
```
log4j.rootCategory=INFO, console
to
log4j.rootCategory=ERROR, console
```
Please, note that those jobs will run forever. In order to shutdown the execution, you can click on the kill button on the 
master's web UI (by default [http://localhost:8080]( http://localhost:8080)).


## References
[1] GDELT Projet: https://www.gdeltproject.org

[2] https://ci.apache.org/projects/flink/flink-docs-release-1.3/quickstart/setup_quickstart.html

[3] https://ci.apache.org/projects/flink/flink-docs-release-1.3/setup/cli.html

[4] https://spark.apache.org/docs/2.2.0/spark-standalone.html
