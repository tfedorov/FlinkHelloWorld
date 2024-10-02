# FlinkHelloWorldApp

### Prerequisites
- Java: Make sure you have JDK 11 or higher installed.
- Gradle: Ensure Gradle is installed and set up properly.
- Flink: This project uses Flink 1.17.0.
- Netcat: Required to simulate a socket server for streaming text data.

## How to start

##  Start a Socket Server
```bash
nc -lk 9999
```


##  Run the Flink Application

```bash
./gradlew run
```