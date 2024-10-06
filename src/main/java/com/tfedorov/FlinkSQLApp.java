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

    public static StreamExecutionEnvironment getEnv() {
        Configuration config = new Configuration();

        // Configure the restart strategy using RestartStrategyOptions
        // Set to noRestart in this case
        config.set(RestartStrategyOptions.RESTART_STRATEGY, "none");

        // Set up the StreamExecutionEnvironment
        return StreamExecutionEnvironment.getExecutionEnvironment(config);
    }

    public static StreamTableEnvironment createTableEnv(StreamExecutionEnvironment env) {

        // Set up the StreamTableEnvironment with the right settings
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode() // For batch, you can use .inBatchMode()
                .build();

        // Create the StreamTableEnvironment
        return StreamTableEnvironment.create(env, settings);
    }

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = getEnv();
        StreamTableEnvironment tableEnv = createTableEnv(env);

        DataStreamSource<String> inputDataStream = env.socketTextStream("localhost", 9999);
        // Convert DataStream to Table
        Table inputView = tableEnv.fromDataStream(inputDataStream);
        tableEnv.createTemporaryView("InputTable", inputView);

        tableEnv.executeSql("CREATE TABLE resultSink (word STRING, counted BIGINT) WITH ( 'connector' = 'filesystem', 'path' = 'file:///Users/tfedorov/IdeaProjects/FlinkHelloWorld/src/main/resources/out', 'format' = 'csv')"); // Fill in your sink configuration
        tableEnv.executeSql("CREATE TEMPORARY VIEW IF NOT EXISTS default_catalog.default_database.word AS SELECT SPLIT(UPPER(f0), ' ')[1] AS word FROM InputTable;");
//        Table resultTable = tableEnv.sqlQuery("SELECT word, Count(*) as counted FROM default_catalog.default_database.word GROUP BY word;");

        Table resultTable = tableEnv.sqlQuery("SELECT word, 1 as counted FROM default_catalog.default_database.word;");

        tableEnv.executeSql("INSERT INTO resultSink SELECT word, 1 FROM default_catalog.default_database.word;");

        // Convert the result table back to a DataStream
        DataStream<Row> resultStream = tableEnv.toAppendStream(resultTable, Row.class);

        // Print the result and execute
        resultStream.print();
        env.execute("Flink SQL Job");

    }


}
