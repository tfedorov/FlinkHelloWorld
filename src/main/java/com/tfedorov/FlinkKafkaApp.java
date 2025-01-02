package com.tfedorov;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

/**
 * Educational Application for Word Counting using Apache Flink and Kafka.
 * <p>
 * This application reads text input from a socket, splits the text into words,
 * counts the occurrences of each word, and writes the results to a Kafka topic.
 */
public class FlinkKafkaApp {

    public static void main(String[] args) throws Exception {
        // Set up the execution environment for Flink
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create a DataStream source from socket text stream on localhost at port 9999
        // Run CLI `nc -lk 9999`
        DataStreamSource<String> inputDataStream = env.socketTextStream("localhost", 9999);

        // Split input text messages into words and collect tuples of (word, 1)
        DataStream<Tuple2<String, Integer>> wordAndOneStream = inputDataStream
                .flatMap((String line, Collector<Tuple2<String, Integer>> out) -> {
                    for (String word : line.split("\\s+")) {
                        out.collect(new Tuple2<>(word, 1));
                    }
                })
                .returns(TypeInformation.of(new TypeHint<>() {
                }));

        // Count occurrences of each word by keying by the word and summing the counts
        DataStream<Tuple2<String, Integer>> wordCounts = wordAndOneStream
                .keyBy(tuple -> tuple.f0)
                .sum(1);

        // Count occurrences of each word by keying by the word and summing the counts
        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("flink-kafka-topic")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .build();

        // Serialize tuples as strings before sending to Kafka
        wordCounts
                .map(tuple -> tuple.f0 + "," + tuple.f1)
                .sinkTo(kafkaSink);

        // Execute the Flink job
        env.execute("Flink Kafka Sink Example");
    }

}
