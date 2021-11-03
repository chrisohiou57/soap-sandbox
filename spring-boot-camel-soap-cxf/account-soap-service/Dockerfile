FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/account-soap-service-1.0.0-SNAPSHOT.jar /opt/app
COPY src/main/resources/telemetry/opentelemetry-javaagent-all.jar /opt/app
CMD ["java", "-javaagent:./opt/app/opentelemetry-javaagent-all.jar", "-jar", "/opt/app/account-soap-service-1.0.0-SNAPSHOT.jar"]