package com.tfedorov;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class FlinkHelloWorldApp {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String filePath = "/Users/tfedorov/IdeaProjects/FlinkHelloWorld/src/main/resources/files2read/test.csv";
        TextInputFormat inputFormat = new TextInputFormat(new Path(filePath));

        SingleOutputStreamOperator<Tuple2<String, Integer>> dataStream = env
//                .socketTextStream("localhost", 9999)
                .<String>readFile(inputFormat, filePath)
                .flatMap(new Splitter())
                .keyBy(value -> value.f0)
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1);

        dataStream.print();

        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word : sentence.split(" ")) {
                System.out.println("sentence = " + sentence + ", out = " + word);
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }
}
