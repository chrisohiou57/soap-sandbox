# Overview
This application is a SOAP API Gateway in our multi-module banking demo project. It is implemented using [Apache Camel](https://camel.apache.org/) and its component libraries. Here is a good excerpt from the book <i>Microservices for [Java Developers, 2nd Edition by Rafael Benevides, Christian Posta](https://www.oreilly.com/library/view/microservices-for-java/9781492038290/)</i> that describes why an application such as this is beneficial.

> As the number of microservices grows, the complexity for the client who is consuming these APIs also grows.
> 
> Real applications could have dozens or even hundreds of microservices. A simple process like buying a book from an online store like Amazon can cause a client (your web browser or your mobile app) to use several other microservices. A client that has direct access to the microservice would have to locate and invoke them and handle any failures they caused itself. So, usually a better approach is to hide those services behind a new service layer. This aggregator service layer is known as an API gateway.
> 
> Another advantage of using an API gateway is that you can add cross-cutting concerns like authorization and data transformation in this layer. Services that use non-internet-friendly protocols can also benefit from the usage of an API gateway. However, keep in mind that it usually isn’t recommended to have a single API gateway for all the microservices in your application. If you (wrongly) decided to take that approach, it would act just like a monolithic bus, violating microservice independence by coupling all the microservices. Adding business logic to an API gateway is a mistake and should be avoided.

# Features
The primary features implemented in this SOAP API Gateway implementation are described below.

## SOAP Proxy
CXF proxies are implemented for the downstream [retail and investment account SOAP APIs](https://github.com/chrisohiou57/soap-sandbox/tree/main/spring-boot-camel-soap-cxf/account-soap-service) which allows us to bind them to Camel routes.

## Enterprise Integration Patterns (EIPs)
One of the benefits that we get by binding our SOAP proxies to Camel routes is that it gives us the ability to implement [EIPs](https://www.enterpriseintegrationpatterns.com/index.html) around them. Apache Camel [supports](https://camel.apache.org/components/3.13.x/eips/enterprise-integration-patterns.html) most of the EIPs, and a few have been used in this application.

- [Multicast](https://camel.apache.org/components/3.13.x/eips/multicast-eip.html) - The `AccountRetrievalRoute` executes parallel remote SOAP API calls to the retail and investment services to get account details. The results are aggregated and the combined result is returned to the client.
- [OnCompletion](cuisinart) - The `AccountCreationRoute` executes a remote SOAP API call to the retail or investment SOAP API, depending on the account type in the request, to create a new account. When the remote downstream API call succeeds the route emits a message to a messaging broker via JMS. If the downstream API call fails the message is not emitted.

## WS-Security
The cross cutting WS-Security concern is implemented in the gateway. WS-Security is not implemented in the downstream retail and investment account SOAP APIs. So, authentication/authorization for clients will happen in the gateway and it will handle communication with downstream APIs that are not protected with WS-Security.

In PROD you would still want to have some protection in place for the downstream APIs. You will want to ensure communication happens over HTTPS. Since the gateway is fronting traffic to back-end APIs you can limit access to these services by not offering ingress paths into your K8s cluster for these services.

## Administration
Hawtio is a pluggable management console for Java stuff which supports any kind of JVM, any kind of container (Tomcat, Jetty, Spring Boot, etc), and any kind of Java technology and middleware. This gateway project is configured to use Hawtio with the [camel management features](https://github.com/hawtio/hawtio-integration/blob/master/plugins/camel/doc/help.md). You can find the URL for the Hawtio console along with all of the other Spring Boot enabled actuator endpoints by running the app and hitting this URL:

http://localhost:8045/actuator

The following properties in `application.properties` have an impact on this functionality:
- `management.endpoints.web.exposure.include` - The following endpoints are exposed with this property.
    - Adding <b>hawtio</b> exposes the Hawtio admin console.
    - Adding <b>jolokia</b> exposes administrative JMX beans over HTTP. Hawtio uses Jolokia for its administrative functionality. Adding this property will also expose it outside of Hawtio.
- `hawtio.authenticationEnabled` - Setting the value to <b>false</b> allows us to access the Hawtio admin console without credentials. This is obviously not suitable for PROD. Maybe an authentication example will be added later.

## Observability
You should also take a look at the [Grafana installation details](https://github.com/chrisohiou57/soap-sandbox/tree/main/spring-boot-camel-soap-cxf/k8s#install-grafana). There you can find some more information about the <i>trace to logs</i> and <i>derrived data source</i> functionality that allow you to jump from logs to traces and vice versa. The important take away for this project is the `logging.pattern.console` property in `application.properties` needs to ensure that a <b>trace_id</b> and <b>span_id</b> is added to the logs.

### Metrics
This gateway application is configured to use the [Camel Micrometer](https://camel.apache.org/components/3.14.x/micrometer-component.html) component to collect metrics from our Camel routes. [Micrometer](https://micrometer.io/) provides simple way to measure the behaviour of an application. You will see code in place to capture custom metrics. Also, the `micrometer-registry-prometheus` library is being used to allow Prometheus to scrape the metrics and ultimately visualize them in Grafana when running in K8s.

The following properties in `application.properties` have an impact on this functionality:
- `management.endpoints.web.exposure.include` - Adding <b>prometheus</b> to this property exposes an endpoint for Prometheus metric scraping.
- `management.metrics.tags.application` - The value for this property will be added as a tag to the captured metrics. This can be used to query metrics in Grafana.

### Tracing
The Dockerfile for this project configures the Camel application to use the [OpenTelemetry](https://github.com/open-telemetry/opentelemetry-java-instrumentation) (otel) Java instrumentation agent. We are also using the [Camel otel component](https://camel.apache.org/components/3.13.x/others/opentelemetry.html) which enables the collection of traces and spans that the otel Java agent can export.

The `OtelConfig` class creates a `OpenTelemetryTracer` bean which enables Camel to capture trace and span data about the routes. The K8s deployment file for this project configures the otel agent to export the distributed trace data to Tempo which is expected to be deployed in the K8s environment via [these instructions](https://github.com/chrisohiou57/soap-sandbox/blob/main/spring-boot-camel-soap-cxf/k8s/README.md). The `logging.pattern.console` property in `application.properties` formats the logs in a way that is suitable for otel. Ultimately, this allows Grafana to include interactions with the gateway in distributed trace visualizations via the Tempo data source.

### Logs
This application is only configured for console logging. This is suitable for running it locally in an IDE. It is also fine for running within containers. The [K8s deployment file](https://github.com/chrisohiou57/soap-sandbox/blob/main/spring-boot-camel-soap-cxf/k8s/acct-svc-camel-deployment.yaml) adds pod annotations which informs Promtail to scrape the pod logs, aka console logs, which ultimately makes thems queryable in Grafana using the Loki data source. If you were to deploy this to a VM or bare metal environment, you would want to configure file logging along with all of the normal constructs such as rolling log files and exporting log file data to a log analysis backend.

## Building the application
To build the project you will need Maven installed, or you can use the Maven wrapper checked in with the project. The command below will generate the Fat JAR.
```
./mvnw clean install
```
Once you have generated the Fat JAR you can build a container image using the Dockerfile in the project. This obviously uses my personal Docker Hub account. You will want to change it to match your account details for whatever image repository you use. This command will use the tag <b><i>latest</i></b> by default.
```
docker build -t chrias/camel-account-soap-service .
```
## Running the application
### Spring Profiles
The application is meant to run under one of two Spring Profiles: <b>retail</b> or <b>investment</b>. See the `application-retail.properties` and `applciation-investment.properties` files to see profile specific configs. Also, note the use of the `@Profile("retail)"` or `@Profile("investment)"` annotations in the `CxfConfigRetail` and `CxfConfigInvestment` classes. This is how the appropriate classes are loaded for the provided profile to simulate different account API back-ends.

The <b>mTLS</b> profile can be used if you have followed the steps below to generate SSL certs for mutual authentication (mTLS). This will set up the appropriate keystore and truststore components for mTLS. See `application-mTLS.properties` file for more details.

### Running in an IDE
The project includes a `launch.json` file in the `.vscode` directory that can be used in VS Code. The <b>Spring Tools 4</b> and <b>Language Support for Java</b> extensions should also be installed. You can follow the [documentation](https://code.visualstudio.com/docs/java/java-spring-boot) to get started with that. You can also easily configure similar run/debug configs in another IDE like IntelliJ by referencing the details in `launch.json`.

### Running the Fat JAR
You can execute the Fat JAR by following the Maven build instructions above and running the following command.
```
java -jar ./target/account-camel-routing-1.0.0.jar --server.port=8045
```

### Running in Containers
There is a Dockerfile in the root of this project that you can use to build an image. See further instruction above. In the k8s directory you will find instructions and K8s YAML files that can be used to deploy to a local K8s cluster like [minikube](https://minikube.sigs.k8s.io/docs/) or K8s bundled in Docker Desktop. This project and its documentation favors K8s over Docker Compose.

This project uses [OpenTelemetry](https://opentelemetry.io/) (otel) for distributed tracing. These docs assume that you are running the app in Kubernetes (K8s) if you are demonstrating the otel functionality. It would be way more trouble than it's worth to configure all of the necessary components outside of K8s, if it's even possible.

#### NOTE
It is not recommended to use the <b>mTLS</b> Spring Profile when running in K8s. A much better way to achieve mTLS in a K8s cluster is using tooling such as [Linkerd](https://linkerd.io/2.9/tasks/securing-your-service/). To set up mTLS for public traffic coming into your K8s cluster you can terminate mTLS at the load balancer fronting your K8s Ingress. So, for running things in a local K8s cluster don't worry about it.