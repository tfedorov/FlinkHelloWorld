package com.tfedorov;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlinkConnectionAppTest {


    @Test
    public void testWindowWordCount() throws Exception {
        MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(
                new MiniClusterResourceConfiguration.Builder()
                        .setNumberTaskManagers(1)
                        .setNumberSlotsPerTaskManager(2)
                        .build());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // Mock input data
        List<String> input = Arrays.asList("hello world", "hello flink", "flink testing");

        DataStream<Tuple2<String, Integer>> dataStream = env
                .fromCollection(input)
                .flatMap(new FlinkConnectionApp.Splitter())
                .keyBy(value -> value.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1);

        // Collect results into a list
        List<Tuple2<String, Integer>> result = dataStream.executeAndCollect(3);

        // Validate the result
        assertEquals(Arrays.asList(
                new Tuple2<>("hello", 2),
                new Tuple2<>("world", 1),
                new Tuple2<>("flink", 2),
                new Tuple2<>("testing", 1)
        ), result);
    }
}
