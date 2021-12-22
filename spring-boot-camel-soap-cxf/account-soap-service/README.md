# Overview
This application is both a retail and investment account SOAP API in our multi-module banking demo project. It is also a demonstration of creating SOAP services in a microservice architectural style with Spring Boot and CXF. The project uses the Spring IoC container and Spring Profiles to inject the appropriate classes to handle retail or investment accounts. In this demo archicture, these services are proxied by a [Camel API Gateway](https://github.com/chrisohiou57/soap-sandbox/blob/main/spring-boot-camel-soap-cxf/account-camel-routing/src/main/resources/application-mTLS.properties) implementation.

## Building the application
To build the project you will need Maven installed, or you can use the Maven wrapper checked in with the project. The command below will generate the Fat JAR.
```
mvnw clean install
```
Once you have generated the Fat JAR you can build a container image using the Dockerfile in the project. This obviously uses my personal Docker Hub account. You will want to change it to match your account details for whatever image repository you use. This command will use the tag <b><i>latest</i></b> by default.
```
docker build -t chrias/account-soap-service .
```

## Running the application
### Spring Profiles
The application is meant to run under one of two Spring Profiles: <b>retail</b> or <b>investment</b>. See the `application-retail.properties` and `applciation-investment.properties` files to see profile specific configs. Also, note the use of the `@Profile("retail)"` or `@Profile("investment)"` annotations in the `CxfConfigRetail` and `CxfConfigInvestment` classes. This is how the appropriate classes are loaded for the provided profile to simulate different account API back-ends.

An additional <b>mTLS</b> profile can be added if you have followed the steps below to generate SSL certs for mutual authentication (mTLS). This will set up the appropriate keystore and truststore components for mTLS. See `application-mTLS.properties` file for more details.

### Application Properties
The application expects several properties to run. These are standard Spring Boot properties.
- <b>spring.profiles.active</b> Valid values are `retail` or `investment`.
- <b>server.port</b> Since the app is expected to run multiple instances under two different profiles you want to be clear about which port each profile is running on.

### Running in an IDE
The project includes a `launch.json` file in the `.vscode` directory that can be used in VS Code. The <b>Spring Tools 4</b> and <b>Language Support for Java</b> extensions should also be installed. You can follow the [documentation](https://code.visualstudio.com/docs/java/java-spring-boot) to get started with that. You can also easily configure similar run/debug configs in another IDE like IntelliJ by referencing the details in `launch.json`.

### Running the Fat JAR
You can execute the Fat JAR by following the Maven build instructions above and running the following command.
```
java -jar ./target/account-soap-service-1.0.0.jar --spring.profiles.active=retail --server.port=8043
```

### Running in Containers
There is a Dockerfile in the root of this project that you can use to build an image. See further instruction above. In the k8s directory you will find instructions and K8s YAML files that can be used to deploy to a local K8s cluster like [minikube](https://minikube.sigs.k8s.io/docs/) or K8s bundled in Docker Desktop. This project and its documentation favors K8s over Docker Compose.

This project uses [OpenTelemetry](https://opentelemetry.io/) (otel) for distributed tracing. These docs assume that you are running the app in Kubernetes (K8s) if you are demonstrating the otel functionality. It would be way more trouble than it's worth to configure all of the necessary components outside of K8s, if it's even possible.

#### NOTE
It is not recommended to use the <b>mTLS</b> Spring Profile when running in K8s. A much better way to achieve mTLS in a K8s cluster is using tooling such as [Linkerd](https://linkerd.io/2.9/tasks/securing-your-service/). To set up mTLS for public traffic coming into your K8s cluster you can terminate mTLS at the load balancer fronting your K8s Ingress. So, for running things in a local K8s cluster don't worry about it.



<!-- NOTE that I had to add imagePullPolicy: Never to pull from my local repo -->
kubectl apply -f acct-svc-retail-deployment.yaml
kubectl apply -f acct-svc-investment-deployment.yaml
kubectl apply -f acct-svc-camel-deployment.yaml
kubectl apply -f acct-svc-subscriber-deployment.yaml
kubectl apply -f opentelemetry-collector-deployment.yaml

<!-- See or remove everything -->
kubectl get all
kubectl delete deployments,pod,svc,statefulset --all

<!-- Remove specific objects -->
kubectl delete svc camel-acct-api-gateway-svc
kubectl delete deployment camel-account-api

<!-- Network tools to troubleshoot -->
kubectl run curl --image=radial/busyboxplus:curl -i --tty
kubectl exec --stdin --tty curl -- sh

<!-- Get a shell to a running pod -->
kubectl exec --stdin --tty ${pod_name} -- /bin/bash
kubectl exec --stdin --tty pod_name -- sh

<!-- View environment variables -->
kubectl exec camel-account-api-6f65bf8967-754jc  -- printenv | grep SERVICE



<!-- Most Basic Loki Grafana Query -->
{app="camel-account-api",namespace="soapdemo"}


k exec camel-account-api-6f65bf8967-754jc  -- printenv | grep SERVICE | grep LOKI
k exec camel-account-api-6f65bf8967-754jc  -- printenv | grep SERVICE | grep TEMPO
k exec loki-grafana-845fb6fd6b-7b79v  -- printenv | grep SERVICE | grep OPEN

k delete svc opentelemetry-collector-svc
k delete deployment opentelemetry-collector



https://opentelemetry.io/docs/concepts/instrumenting/
Once you’ve created telemetry data, you’ll want to send it somewhere. OpenTelemetry supports two primary methods of exporting data from your process to an analysis backend, either directly from a process or by proxying it through the OpenTelemetry Collector.

After installing the Loki stack logs are automagically getting sent to Loki and are queryable in Grafana with just this config. I do not see the log hitting the otel-collector.
        env:
        - name: API_URI_SCHEME
          value: http
        - name: OTEL_RESOURCE_ATTRIBUTES
          value: service.name=camel-account-api

<!-------------------------------------------------------------->
<!-------------- INSTALL OBSERVABILITY COMPONENTS -------------->
<!-------------------------------------------------------------->
<!-- Add the Grafana Helm repo -->
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

<!-- Install Tempo (Single Binary): https://github.com/grafana/tempo/tree/main/example/helm -->
helm upgrade --install tempo grafana/tempo

<!-- Install the loki-stack -->
helm upgrade --install loki grafana/loki-stack -f .\loki-stack-values.yaml

<!-- Install Grafana with predefined data sources -->
helm install grafana grafana/grafana -f .\grafana-values.yaml





<!-------------------------------------------------------------->
<!-------------------- DEPLOY SOAP SERVICES -------------------->
<!-------------------------------------------------------------->
kubectl apply -f acct-svc-retail-deployment.yaml
kubectl apply -f acct-svc-investment-deployment.yaml
kubectl apply -f acct-svc-camel-deployment.yaml







kubectl apply -n tracing -f https://raw.githubusercontent.com/antonioberben/examples/master/opentelemetry-collector/otel.yaml
kubectl apply -n tracing -f - << 'EOF'
apiVersion: v1
kind: ConfigMap
metadata:
 name: otel-collector-conf
 labels:
   app: opentelemetry
   component: otel-collector-conf
data:
 otel-collector-config: |
   receivers:   
     zipkin:
       endpoint: 0.0.0.0:9411
   exporters:
     otlp:
       endpoint: tempo.tracing.svc.cluster.local:55680
       insecure: true
   service:
     pipelines:
       traces:
         receivers: [zipkin]
         exporters: [otlp]
EOF



<!-- Up Next -->
https://github.com/docker/for-win/issues/10038





<!-- Counter Metric Prometheus query -->
camelAccountApiCounter_total{application="camel-account-api"}

<!-- Start local Artemis environment -->
cd C:\dev\apache-artemis-2.19.0\bin
C:\usr\soapdemo\bin\artemis.cmd run