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
/path/to/flink/root/bin/flink run hackathon-flink-java/target/hackathon-flink-java-0.1-SNAPSHOT.jar --path /path/to/data/180-days.csv

# Scala Job
/path/to/flink/root/bin/flink run hackathon-flink-scala/target/hackathon-flink-scala-0.1-SNAPSHOT.jar --path /path/to/data/180-days.csv
```
Please, note that those jobs will run forever. In order to shutdown the execution, you need to prompt
```
/path/to/flink/root/bin/flink stop <jobID>
```
as explained here [3]

### Apache Spark

## References
[1] GDELT Projet: https://www.gdeltproject.org
[2] https://ci.apache.org/projects/flink/flink-docs-release-1.3/quickstart/setup_quickstart.html
[3] https://ci.apache.org/projects/flink/flink-docs-release-1.3/setup/cli.html
