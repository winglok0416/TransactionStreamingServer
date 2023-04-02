## Transaction Streaming Server
This is the server to stream from Kafka with topic - **transaction** and to perform analysis with Spark

Before running this program, please make sure you have following the instruction to set up the kafka brokers and spark.

***Please build the jar with JRE 1.8.0_332 to make sure the Spark container can run***
***The built program cannot be run without Spark environment***

### Install dependencies and build
```
gradle clean build
```

### Execute bash of the docker container running Spark
```
docker exec -it parallel-assignment2-spark-1 /bin/bash
```

### Submit the task to Spark
```
spark-submit --class hk.edu.cuhk.TransactionStreamingServer --master local[3] /test-files/transaction-streaming-server/build/libs/TransactionStreamingServer-1.0-SNAPSHOT.jar
```

### Read the output
```
# Show the output
cat /opt/bitnami/spark/tmp/result.txt

# Monitoring the output file
tail -f -n 20 /opt/bitnami/spark/tmp/result.txt
```