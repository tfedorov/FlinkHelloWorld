package com.tfedorov;

import com.tfedorov.functions.Functions;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.execution.SavepointFormatType;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.concurrent.CompletableFuture;

public class WordCountLocalApp {

    public static Boolean stopAppIndicate = false;

    public static void main(String[] args) throws Exception {
        // Set up the execution environment for Flink
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Run nc -l 9999
        DataStream<String> socketLinesStream = env.socketTextStream("localhost", 9999);
        SingleOutputStreamOperator<String> wordsStream = socketLinesStream.flatMap((String line, Collector<String> out) -> {
            for (String word : line.split("\\s+")) {
                out.collect(word);
            }
        }).returns(TypeInformation.of(new TypeHint<>() {
        }));

        var countFunction = new Functions.LocalWordCountFunction();
        wordsStream.keyBy(word -> word.toLowerCase().hashCode() % 2).flatMap(countFunction).print();  // or

        JobClient wordCountLocalAppClient = env.executeAsync("WordCountLocalApp Example");
        new Thread(() -> {
            while (true) {
                if (stopAppIndicate)
                    wordCountLocalAppClient.stopWithSavepoint(true,
                                    "/Users/tfedorov/IdeaProjects/private/FlinkHelloWorld/src/main/resources/savepoint",
                                    SavepointFormatType.DEFAULT)
                            .thenAccept(path -> System.out.println("Stopped with savepoint at " + path)).complete(null);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();

    }

}
