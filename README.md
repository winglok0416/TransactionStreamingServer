## Transaction Streaming Server
This is the server to stream from Kafka with topic - **transaction** and to perform analysis with Spark

Before running this program, please make sure you have following the instruction to set up the kafka brokers and spark.

***Please build the jar with JDK 1.8.0_332 to make sure the Spark container can run***
***The built program cannot be run without Spark environment***


### Environment
Operating System: MacOS 13.3 or Ubuntu 18

Dependencies:
Java Development Kit 1.8.0
Gradle 7.2

### Install dependencies and build
```
gradle clean build
```

### Copy build/libs/TransactionStreamingServer-1.0-SNAPSHOT.jar to the directory of transaction-streaming-infra

### Execute bash of the docker container running Spark
*maybe "transaction-streaming-infra_spark_1" if you are running on ubuntu*
```
docker exec -it transaction-streaming-infra-spark-1 /bin/bash
```

### Submit the task to Spark
```
spark-submit --class hk.edu.cuhk.TransactionStreamingServer --master local[3] /test-files/TransactionStreamingServer-1.0-SNAPSHOT.jar
```

### Read the output
```
# Show the output
cat /opt/bitnami/spark/tmp/result.txt

# Monitoring the output file
tail -f -n 20 /opt/bitnami/spark/tmp/result.txt
```