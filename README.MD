# Flink Kafka Word Count Application

## Overview

This is an educational application designed to demonstrate word counting using Apache Flink and Kafka. The application reads text input from a socket, splits the text into words, counts the occurrences of each word, and writes the results to a Kafka topic.

## Prerequisites

Before running this application, ensure you have the following installed:

- Java 8 or higher
- Gradle
- Docker (for Kafka setup using Docker)
- nc command on your OS

## Setup

### Kafka Setup with Docker

1. **Install Docker:**
- Download and install Docker from [Docker's official website](https://www.docker.com/get-started).
- **Verification:** Run `docker --version` to verify Docker is installed correctly. You should see the Docker version information.
2. **Start Kafka and Zookeeper**:
```bash
docker-compose up -d
```
- Verification: Run docker ps to verify that both Kafka and Zookeeper containers are running. You should see both zookeeper and kafka containers listed.
3. **Create a Kafka Topic (optional)**:
```bash
docker exec -it kafka kafka-topics.sh --create --topic flink-kafka-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```
- Verification: Run `docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092` to verify the topic is created. You should see flink-kafka-topic listed.

4. **Run the Application**:
```bash
./gradlew build
./gradlew run
```
- Verification: Ensure the application is running by checking the output. You should see logs indicating that the Flink job has started
5. **Send Data to the Socket**:
```bash
nc localhost 9999
```
- Type some words and press Enter.

### Expected result:
To verify that the word counts are being written to the Kafka topic, run the following command:
```bash
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic flink-kafka-topic --from-beginning
```
- Verification: You should see the words and their counts being printed in the terminal. For example, if you typed "hello world hello", you should see:
```bash
hello,1
world,1
hello,2
```

Conclusion