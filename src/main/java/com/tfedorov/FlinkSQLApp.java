package com.tfedorov;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


public class FlinkSQLApp {

    public static void main(String[] args) throws Exception {
        // Set up the StreamExecutionEnvironment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.noRestart()); // For development/debug purposes

        // Set up the StreamTableEnvironment with the right settings
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode() // For batch, you can use .inBatchMode()
                .build();

        // Create the StreamTableEnvironment
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);

        // DataStream source
        DataStream<String> dataStream = env.fromElements("Alice", "Bob", "John");

        // Convert DataStream to Table
        tableEnv.createTemporaryView("InputTable", tableEnv.fromDataStream(dataStream));

        // Execute SQL query on the table
        Table resultTable = tableEnv.sqlQuery("SELECT UPPER(f0) FROM InputTable");

        // Convert the result table back to a DataStream
        DataStream<Row> resultStream = tableEnv.toDataStream(resultTable);

        // Print the result and execute
        resultStream.print();
        env.execute("Flink SQL Job");
//        JsonPath.isPathDefinite("");
    }
}
