package com.tfedorov;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlinkConnectionAppTest {

    @Test
    public void testSumStream() throws Exception {
        MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(
                new MiniClusterResourceConfiguration.Builder()
                        .setNumberTaskManagers(2)
                        .setNumberSlotsPerTaskManager(2)
                        .build());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // Mock input data
        List<String> input = asList("hello world", "hello flink", "flink testing");

        DataStreamSource<String> inputStream = env.fromCollection(input);

        SingleOutputStreamOperator<Tuple2<String, Integer>> actualStream = FlinkConnectionApp.sumStream(inputStream);
        actualStream.print();
        actualStream.getTransformation();
        List<Tuple2<String, Integer>> actual = actualStream
                .executeAndCollect(100).stream().sorted(Comparator.comparing(tuple -> tuple.f0))
                .collect(Collectors.toList());

        List<Tuple2<String, Integer>> expected = Stream.of(
                        new Tuple2<>("testing", 1),
                        new Tuple2<>("hello", 1),
                        new Tuple2<>("hello", 2),
                        new Tuple2<>("world", 1),
                        new Tuple2<>("flink", 1),
                        new Tuple2<>("flink", 2)
                ).sorted(Comparator.comparing(tuple -> tuple.f0))
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    public void testFlatMap() throws Exception {
        // Arrange
        FlinkConnectionApp.Splitter splitter = new FlinkConnectionApp.Splitter();
        // Create a mock collector to collect the output
        List<Tuple2<String, Integer>> collectedItems = new ArrayList<>();

        Collector<Tuple2<String, Integer>> collector = new Collector<Tuple2<String, Integer>>() {
            @Override
            public void collect(Tuple2<String, Integer> element) {
                collectedItems.add(element);
            }

            @Override
            public void close() {
                // No operation
            }
        };

        // Act
        String inputSentence = "hello world hello";
        splitter.flatMap(inputSentence, collector);

        // Assert
        List<Tuple2<String, Integer>> expectedOutput = asList(new Tuple2<>("hello", 1), new Tuple2<>("world", 1), new Tuple2<>("hello", 1));

        assertEquals(expectedOutput, collectedItems);

    }
}
