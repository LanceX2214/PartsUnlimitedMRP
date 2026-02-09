# PartsUnlimitedMRP + Kieker Instrumentation

This repository is instrumented with Kieker using AspectJ load-time weaving, similar to the JPetStore tutorial flow.

## What Was Added

- `src/Backend/OrderService/build.gradle`
- `src/Backend/IntegrationService/build.gradle`
  - Added dependencies:
    - `net.kieker-monitoring:kieker:1.14`
    - `org.aspectj:aspectjrt:1.9.7`
    - `org.aspectj:aspectjweaver:1.9.7`
- `src/Backend/OrderService/src/main/resources/META-INF/aop.xml`
- `src/Backend/IntegrationService/src/main/resources/META-INF/aop.xml`
  - Enables `OperationExecutionAspectFull`
- `src/Backend/OrderService/src/main/resources/META-INF/kieker.monitoring.properties`
- `src/Backend/IntegrationService/src/main/resources/META-INF/kieker.monitoring.properties`
  - Enables filesystem trace log writing
- Graceful flush on shutdown:
  - `src/Backend/OrderService/src/main/java/smpl/ordering/OrderingConfiguration.java`
  - `src/Backend/OrderService/src/main/java/smpl/ordering/OrderingInitializer.java`
  - `src/Backend/IntegrationService/src/main/java/integration/Main.java`

## Build

From each backend module directory:

```bash
./gradlew clean build
```

## Run With Instrumentation

Kieker AspectJ weaving requires the AspectJ Java agent.

1. Locate the AspectJ weaver jar:

```bash
ASPECTJ_WEAVER_JAR=$(find "$HOME/.gradle/caches/modules-2/files-2.1/org.aspectj/aspectjweaver/1.9.7" -name 'aspectjweaver-1.9.7.jar' | head -n 1)
```

2. Run `OrderService` with weaving:

```bash
cd src/Backend/OrderService
mkdir -p build/kieker-traces/order-service
java -javaagent:"$ASPECTJ_WEAVER_JAR" \
  -Dkieker.monitoring.writer.filesystem.FileWriter.customStoragePath=build/kieker-traces/order-service \
  -jar build/libs/ordering-service-0.1.0.jar
```

3. Run `IntegrationService` with weaving:

```bash
cd src/Backend/IntegrationService
mkdir -p build/kieker-traces/integration-service
java -javaagent:"$ASPECTJ_WEAVER_JAR" \
  -Dkieker.monitoring.writer.filesystem.FileWriter.customStoragePath=build/kieker-traces/integration-service \
  -jar build/libs/integration-service-0.1.0.jar
```

If deployed to Tomcat/Jetty, add the same `-javaagent:/path/to/aspectjweaver-1.9.7.jar` into container JVM options.

## Generated Trace Logs

By default, Kieker writes logs into its auto-generated temp directory (printed in service logs as `actualStoragePath`).

You can override output directory at runtime (directory must already exist):

```bash
-Dkieker.monitoring.writer.filesystem.FileWriter.customStoragePath=/absolute/path/to/your-traces
```

## Trace Analysis (Kieker 1.14)

Place `trace-analysis-1.14/` under project root, then run:

```bash
trace-analysis-1.14/bin/trace-analysis \
  --inputdirs /path/to/your-traces \
  --outputdir out/order-trace \
  --plot-Assembly-Component-Dependency-Graph responseTimes-ns

dot out/order-trace/assemblyComponentDependencyGraph.dot -T pdf -o out/order-trace/assemblyComponentDependencyGraph.pdf
```

Other useful plots/options:

- `--plot-Assembly-Operation-Dependency-Graph`
- `--plot-Deployment-Operation-Dependency-Graph`
- `--print-Execution-Traces`
- `--print-System-Model`
- `--include-self-loops`
- `--short-labels`

Show all options:

```bash
trace-analysis-1.14/bin/trace-analysis --help
```

## Java Notes

- Build/runtime compatibility for this legacy project depends on your local Gradle/JDK setup.
- Verified build in this repo with Java 8 (`8.0.472-amzn`) due legacy Gradle wrappers (2.1 and 1.11).
- Kieker Trace Analysis 1.14 is typically easiest on Java 8 or Java 11.
