package com.tfedorov.functions;

import com.tfedorov.WordCountLocalApp;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Functions {

    public static class LocalWordCountFunction extends RichFlatMapFunction<String, String> {

        private Map<String, Integer> wordCounts;


        @Override
        public void open(Configuration parameters) throws Exception {
            // Initialize in-memory map
            this.wordCounts = new HashMap<>();
        }

        @Override
        public void flatMap(String value, Collector<String> out) throws Exception {
            if ("exit".equalsIgnoreCase(value)) {
                System.out.println("Is exit on function");
                WordCountLocalApp.stopAppIndicate = true;
            }
            // Assume value is already a token/word
            wordCounts.merge(value.toLowerCase(), 1, Integer::sum);

            // You can emit intermediate results, e.g., the updated count
            out.collect(value + "=" + wordCounts.get(value.toLowerCase()));
            System.out.println("Final local wordCounts: " + wordCounts + ", " + value.toLowerCase().hashCode() % 2);
        }

        @Override
        public void close() throws Exception {
            // Optionally, print final results or write them somewhere
            System.out.println("Final local wordCounts: " + wordCounts);
        }
    }
}