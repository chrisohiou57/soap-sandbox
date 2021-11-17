FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/account-event-subscriber-0.0.1-SNAPSHOT.jar /opt/app

COPY src/main/resources/telemetry/opentelemetry-javaagent-all.jar /opt/app
CMD ["java", "-javaagent:./opt/app/opentelemetry-javaagent-all.jar", "-jar", "/opt/app/account-event-subscriber-0.0.1-SNAPSHOT.jar"]

# ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opt/app
# CMD ["java", "-javaagent:./opt/app/opentelemetry-javaagent.jar", "-jar", "/opt/app/account-event-subscriber-0.0.1-SNAPSHOT.jar"]