package com.tfedorov;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestartStrategyOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


public class FlinkSQLApp {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();

        // Configure the restart strategy using RestartStrategyOptions
        // Set to noRestart in this case
        config.set(RestartStrategyOptions.RESTART_STRATEGY, "none");

        // Set up the StreamExecutionEnvironment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        // Set up the StreamTableEnvironment with the right settings
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode() // For batch, you can use .inBatchMode()
                .build();

        // Create the StreamTableEnvironment
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        DataStreamSource<String> inputDataStream = env.socketTextStream("localhost", 9999);

        // Convert DataStream to Table
        Table inputView = tableEnv.fromDataStream(inputDataStream);
        tableEnv.createTemporaryView("InputTable", inputView);

        // Execute SQL query on the table
        Table resultTable = tableEnv.sqlQuery("SELECT SPLIT(UPPER(f0), ' ') AS word FROM InputTable");

        // Convert the result table back to a DataStream
        DataStream<Row> resultStream = tableEnv.toDataStream(resultTable);

        // Print the result and execute
        resultStream.print();
        env.execute("Flink SQL Job");

    }
}
