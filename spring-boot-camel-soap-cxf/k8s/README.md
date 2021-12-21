# Overview
Here you will find instructions for deploying the observability stack and application components to a local Kubernetes environment. In order to follow these instruction you will need to have installed [Helm](https://helm.sh) and a Kubernetes environment on your machine along with the kubectl CLI tooling.

The observability stack is comprised of the following components:
- <b>[Grafana Loki Stack](https://grafana.com/docs/loki/latest/):</b> is a set of components that can be composed into a fully featured logging stack. The individual components are discuss in more detail below.
    - <b>Loki:</b> Unlike other logging systems, Loki is built around the idea of only indexing metadata about your logs: labels (just like Prometheus labels). Typically, log data itself is then compressed and stored in chunks in object stores such as S3 or GCS, or even locally on the filesystem. However, for this local example the data isn't being stored in this fashion. Your data will be lost when the stack is deleted from your local K8s environment, or your machine is restarted.
    - <b>[Promtail](https://grafana.com/docs/loki/latest/clients/promtail/):</b> Promtail is an agent which ships the contents of local logs to a private Grafana Loki instance. Is primarily discovers targets, attaches labels to log streams, and pushes them to the Loki instance.
    - <b>[Prometheus](https://prometheus.io/docs/introduction/overview/):</b> Prometheus is an open-source systems monitoring and alerting toolkit.
    - <b>[Grafana](https://grafana.com/grafana/):</b> Grafana allows you to query, visualize, and alert on your log data.
- <b>[Grafan Tempo](https://grafana.com/oss/tempo/):</b> Tempo is an open source high-scale distributed tracing backend. It only requires object storage to operate, and is deeply integrated with Grafana, Prometheus, and Loki. Tempo can be used with any of the open source tracing protocols, including Jaeger, Zipkin, and OpenTelemetry.

## Install Loki Stack
Create a namespace to deploy observability components to:
```
kubectl create namespace observe
```
Add the Helm repository:
```
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
```
Install the loki-stack with the a customized values file:
```
helm upgrade --install loki grafana/loki-stack -f loki-stack-values.yaml -n observe
```
The custom values file disables Fluent Bit in favor of Promtail for sending logs to Loki. We also disable persistent storage for a local environment.
## Install Tempo
Install Tempo using default values:
```
helm upgrade --install tempo grafana/tempo -n observe
```
## Install Grafana
Install Grafana with a custom values file:
```
helm upgrade --install grafana grafana/grafana -f grafana-values.yaml -n observe
```
The values file sets the Grafana web app credentials and defines 3 data sources:
- <b>Tempo:</b> This is where the otel instrumented applications are exporting metrics to. The <i>traces to logs</i> feature is enabled, and uses the K8s namespace, trace ID, and span ID to construct a Loki query. Ultimately, this allows you to easily display the logs associated with a distributed trace.
- <b>Loki:</b> This is where our applications that are annotated for promtail log scraping are sending pod logs to. A derived field (traceId) is configured which is indexed by Loki and allows us to display Tempo traces associated with the trace IDs in our logs.
- <b>Prometheus:</b> The Artemis broker has been configured to export metrics with the Prometheus plugin. This data source allows us to query and visualize them.
















<!-- Build the JAR -->
mvn clean install -DskipTests

<!-- Build the image, and optionally, run it in Docker. -->
docker build -t chrias/account-soap-service .
docker run -p 8043:8043 -e SPRING_PROFILES_ACTIVE=retail chrias/account-soap-service
docker run -p 8043:8043 -e SPRING_PROFILES_ACTIVE=investment chrias/account-soap-service

<!-- Build the image, and optionally, run it in Docker. -->
docker build -t chrias/camel-account-soap-service .
docker run -p 8043:8043 -e INVESTMENT_SOAP_API_URI=https://10.105.78.35:8043/investment/account -e RETAIL_SOAP_API_URI=https://10.105.78.34:8043/retail/account chrias/camel-account-soap-service
docker run -p 8043:8043 chrias/camel-account-soap-service

<!-- Add the Grafana Helm repo -->
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

<!-- Install Tempo (Single Binary): https://github.com/grafana/tempo/tree/main/example/helm -->
helm upgrade --install tempo grafana/tempo

<!-- Install the loki-stack -->
helm upgrade --install loki grafana/loki-stack -f .\loki-stack-values.yaml

<!-- Install Grafana with predefined data sources -->
helm install grafana grafana/grafana -f .\grafana-values.yaml

<!-- Deploy SOAP services -->
kubectl apply -f acct-svc-retail-deployment.yaml
kubectl apply -f acct-svc-investment-deployment.yaml
kubectl apply -f acct-svc-camel-deployment.yaml

<!-- Run a basic Grafana Loki query after hitting the camel-account-api in SOAP UI. -->
{app="camel-account-api",namespace="soapdemo"}


kubectl create deployment retail-account-svc --image=chrias/account-soap-service --dry-run=client -o=yaml > retail-acct-svc-deployment.yaml
echo --- >> retail-acct-svc-deployment.yaml
<!-- kubectl create service nodeport retail-account-svc-nodeport --tcp=8080:8080 --dry-run=client -o=yaml >> retail-acct-svc-deployment.yaml -->

<!-- This is what I used to create the NodePort service. I got the yaml by running: kubectl get service retail-account-svc -o yaml -->
kubectl expose deployment retail-account-svc --name=nodeport --port=8080 --target-port=8080 --type=NodePort

<!-- NOTE that I had to add imagePullPolicy: Never to pull from my local repo -->
kubectl apply -f acct-svc-retail-deployment.yaml
kubectl apply -f acct-svc-investment-deployment.yaml
kubectl apply -f acct-svc-camel-deployment.yaml
kubectl apply -f opentelemetry-collector-deployment.yaml

<!-- See or remove everything -->
kubectl get all
kubectl delete deployments,pod,svc,statefulset,daemonset,configmap --all

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


java -javaagent:./src/main/resources/telemetry/opentelemetry-javaagent-all.jar -jar .\target\account-camel-routing-1.0.0-SNAPSHOT.jar --server.port=8045 --api.uri.scheme=http --investment.acct.api.svc.service.host=localhost --investment.acct.api.svc.service.port=8044 --retail.account.api.svc.service.host=localhost --retail.account.api.svc.service.port=8043




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