k delete -f test-app-deployment.yaml
k delete -f collector-deployment.yaml
k delete -f collector-config-maps.yaml
k delete -f collector-service.yaml
helm uninstall grafana


helm install my-opentelemetry-collector open-telemetry/opentelemetry-collector -f ./otel-helm-values.yaml
helm uninstall my-opentelemetry-collector

curl -v -H "Content-Type: application/json" -XPOST -s "http://loki.soapdemo.svc.cluster.local:3100/loki/api/v1/push" -d '{"streams": [{ "stream": { "foo": "bar2" }, "values": [ [ "1638416396912000000", "fizzbuzz" ] ] }]}'