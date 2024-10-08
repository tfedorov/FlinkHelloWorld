package com.tfedorov;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkPersonApp {

    public static class Person {
        public String name;
        public Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String toString() {
            return this.name.toString() + ": age " + this.age.toString();
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setString("execution.target", "local");
        configuration.setString("taskmanager.memory.process.size", "1024m");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(2, configuration);


        DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 35),
                new Person("Pebbles", 2));

        DataStream<Person> adults = flintstones.filter(new FilterFunction<Person>() {
            @Override
            public boolean filter(Person person) throws Exception {
                return person.age >= 18;
            }
        });

        adults.print();

        env.execute("Flink HelloWorld Job");

    }
}
