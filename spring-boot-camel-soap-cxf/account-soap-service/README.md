#### Create certificates and keystore/trustore for mutual SSL handshakes

##### Create self signed certificate for retail SOAP webservice app:
keytool -genkeypair -alias retail-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore retail-app.jks -dname "CN=localhost, OU=Retail, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass retailpw -keypass retailpw -validity 3650

##### Create public certificate from retail app certificate:
keytool -export -alias retail-app -file retail-app.crt -keystore retail-app.jks

##### Create self signed certificate for investments SOAP webservice app:
keytool -genkeypair -alias investments-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore investments-app.jks -dname "CN=localhost, OU=Investments, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass investmentspw -keypass investmentspw -validity 3650

##### Create public certificate from investments app certificate:
keytool -export -alias investments-app -file investments-app.crt -keystore investments-app.jks

##### Create self signed certificate for Camel app:
keytool -genkeypair -alias camel-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore camel-app.jks -dname "CN=localhost, OU=Middleware, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass camelpw -keypass camelpw -validity 3650

##### Create public certificate from Camel app certificate:
keytool -export -alias camel-app -file camel-app.crt -keystore camel-app.jks

##### Create self signed certificate for client app:
keytool -genkeypair -alias client-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore client-app.jks -dname "CN=localhost, OU=Online Banking, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass clientpw -keypass clientpw -validity 3650

##### Create public certificate from client app certificate:
keytool -export -alias client-app -file client-app.crt -keystore client-app.jks

#### Import certaificates on both sides for mutual SSL handshakes

##### Import retail-app certificate into camel-app and client-app keystore:
keytool -import -trustcacerts -alias retail-app -file retail-app.crt -keystore camel-app.jks
keytool -import -trustcacerts -alias retail-app -file retail-app.crt -keystore client-app.jks

##### Import camel-app and client-app certificate into retail-app keystore:
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore retail-app.jks
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore retail-app.jks

##### Import investment-app certificate into camel-app and client-app keystore:
keytool -import -trustcacerts -alias investment-app -file investment-app.crt -keystore camel-app.jks
keytool -import -trustcacerts -alias investment-app -file investment-app.crt -keystore client-app.jks

##### Import camel-app and client-app certificate into investment-app keystore:
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore investment-app.jks
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore investment-app.jks

##### Import camel-app certificate into client-app keystore:
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore client-app.jks

##### Import client-app certificate into camel-app keystore:
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore camel-app.jks

mvn clean install -DskipTests

docker image ls | grep chrias
docker ps | grep chrias
docker kill ${CONTAINER_ID}

docker build -t chrias/account-soap-service .
docker run -p 8043:8043 -e SPRING_PROFILES_ACTIVE=retail chrias/account-soap-service
docker run -p 8043:8043 -e SPRING_PROFILES_ACTIVE=investment chrias/account-soap-service

docker build -t chrias/camel-account-soap-service .
docker run -p 8043:8043 -e INVESTMENT_SOAP_API_URI=https://10.105.78.35:8043/investment/account -e RETAIL_SOAP_API_URI=https://10.105.78.34:8043/retail/account chrias/camel-account-soap-service
docker run -p 8043:8043 chrias/camel-account-soap-service



kubectl create deployment retail-account-svc --image=chrias/account-soap-service --dry-run=client -o=yaml > retail-acct-svc-deployment.yaml
echo --- >> retail-acct-svc-deployment.yaml
<!-- kubectl create service nodeport retail-account-svc-nodeport --tcp=8080:8080 --dry-run=client -o=yaml >> retail-acct-svc-deployment.yaml -->

<!-- This is what I used to create the NodePort service. I got the yaml by running: kubectl get service retail-account-svc -o yaml -->
kubectl expose deployment retail-account-svc --name=nodeport --port=8080 --target-port=8080 --type=NodePort

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


java -javaagent:"C:\dev\personal\vscode\otel-extensions\build\libs\opentelemetry-javaagent.jar" -Dotel.javaagent.extensions="C:\dev\personal\vscode\otel-extensions\build\libs\otel-extensions-1.0.0-SNAPSHOT-all.jar" -jar ./target/account-camel-routing-1.0.0-SNAPSHOT.jar --server.port=8045 --api.uri.scheme=http --investment.acct.api.svc.service.host=localhost --investment.acct.api.svc.service.port=8044 --retail.account.api.svc.service.host=localhost --retail.account.api.svc.service.port=8043

java -javaagent:./src/main/resources/telemetry/opentelemetry-javaagent-all.jar -Dotel.javaagent.extensions="C:\dev\personal\vscode\otel-extensions\build\libs\otel-extensions-1.0.0-SNAPSHOT-all.jar" -jar ./target/account-camel-routing-1.0.0-SNAPSHOT.jar --server.port=8045 --api.uri.scheme=http --investment.acct.api.svc.service.host=localhost --investment.acct.api.svc.service.port=8044 --retail.account.api.svc.service.host=localhost --retail.account.api.svc.service.port=8043

java -javaagent:./src/main/resources/telemetry/opentelemetry-javaagent-all.jar -jar .\target\account-soap-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=retail --server.port=8043

java -javaagent:./src/main/resources/telemetry/opentelemetry-javaagent-all.jar -jar .\target\account-event-subscriber-0.0.1-SNAPSHOT.jar


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