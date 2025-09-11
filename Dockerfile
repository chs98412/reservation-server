FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar .
ENTRYPOINT ["java","-jar","app.jar"]
