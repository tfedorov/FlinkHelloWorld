/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * To learn more about Gradle by exploring our Samples at https://docs.gradle.org/8.8/samples
 */

plugins {
    java
    id("idea")
    id("com.gradleup.shadow") version "8.3.2" // Applying the 'shadow' plugin to create fat/uber JARs with dependencies
    application
}

repositories {
    // Maven Central Repository
    mavenCentral()
}

dependencies {
    implementation("org.apache.flink:flink-java:1.17.0")
    implementation("org.apache.flink:flink-streaming-java:1.17.0")
    implementation("org.apache.flink:flink-core:1.17.0")

    testImplementation("org.apache.flink:flink-test-utils:1.17.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")    // Adding JUnit Jupiter API for writing unit tests
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")    // Adding JUnit Jupiter Engine for running tests

}
application {
    // Replace this with your main class name
    mainClass.set("com.tfedorov.FlinkHelloWorldApp")
}


tasks.test {
    useJUnitPlatform() // Configuring the 'test' task to use the JUnit platform (JUnit 5)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")  // Adding a compiler argument to show warnings for deprecated APIs
}
