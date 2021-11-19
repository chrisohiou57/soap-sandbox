FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/account-camel-routing-1.0.0-SNAPSHOT.jar /opt/app
COPY src/main/resources/telemetry/opentelemetry-javaagent-custom-ext.jar /opt/app
CMD ["java", "-javaagent:./opt/app/opentelemetry-javaagent-custom-ext.jar", "-jar", "/opt/app/account-camel-routing-1.0.0-SNAPSHOT.jar"]