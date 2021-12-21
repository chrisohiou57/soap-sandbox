# Overview
[Mutual Authentication](https://en.wikipedia.org/wiki/Mutual_authentication) (mTLS) is a common security measure put in place to ensure that your application trusts third-party applications that wish to communicate with it over HTTPS. This can be simulated in a local environment using self-signed certificates. The instructions below demonstrate how to generate the artifacts required to set up mTLS for a Spring Boot application, or any Java application for that matter.

The commands below generate the artifacts required to configure mutual authentication between the web applications in this multi-module project. To see the configurations using these artifacts please see these projects:
- [account-soap-service](https://github.com/chrisohiou57/soap-sandbox/blob/main/spring-boot-camel-soap-cxf/account-soap-service/src/main/resources/application-mTLS.properties)
- [account-camel-routing](https://github.com/chrisohiou57/soap-sandbox/blob/main/spring-boot-camel-soap-cxf/account-camel-routing/src/main/resources/application-mTLS.properties)

## Disclaimer
While you could, it is unlikely that you will configure applications like this in PROD. This is just a good exercise for understanding what mTLS is, and an exploration of the native Spring Boot support for SSL. In an enterprise setting, you would likely be terminating mTLS at the API Gateway or Load Balancer fronting your applications.

Using tools such as [Linkerd](https://linkerd.io/2.9/tasks/securing-your-service/) would be a much better way to achieve mTLS within a K8s cluster. To set up mTLS for public traffic coming into your K8s cluster you can terminate mTLS at the load balancer fronting your K8s Ingresses. So, for running things in a local K8s cluster don't even worry about it.

# Create SSL Certificates & Keystore/Trustore for Mutual Authentication (mTLS)

## Retail App

### Create self signed certificate for retail SOAP webservice app:
```
keytool -genkeypair -alias retail-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore retail-app.jks -dname "CN=localhost, OU=Retail, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass retailpw -keypass retailpw -validity 3650
```

### Create public certificate from retail app certificate:
```
keytool -export -alias retail-app -file retail-app.crt -keystore retail-app.jks
```

## Investments App

### Create self signed certificate for investments SOAP webservice app:
```
keytool -genkeypair -alias investments-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore investments-app.jks -dname "CN=localhost, OU=Investments, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass investmentspw -keypass investmentspw -validity 3650
```

### Create public certificate from investments app certificate:
```
keytool -export -alias investments-app -file investments-app.crt -keystore investments-app.jks
```

## Camel App

### Create self signed certificate for Camel app:
```
keytool -genkeypair -alias camel-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore camel-app.jks -dname "CN=localhost, OU=Middleware, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass camelpw -keypass camelpw -validity 3650
```

### Create public certificate from Camel app certificate:
```
keytool -export -alias camel-app -file camel-app.crt -keystore camel-app.jks
```

## Client App

### Create self signed certificate for client app:
```
keytool -genkeypair -alias client-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore client-app.jks -dname "CN=localhost, OU=Online Banking, O=Chrias Corp, L=Columbus, S=Ohio, C=US" -storepass clientpw -keypass clientpw -validity 3650
```

### Create public certificate from client app certificate:
```
keytool -export -alias client-app -file client-app.crt -keystore client-app.jks
```

# Import Certaificates on Both Sides for mTLS

## Camel and Client Apps Trust Retail App

### Import retail-app certificate into camel-app and client-app keystore:
```
keytool -import -trustcacerts -alias retail-app -file retail-app.crt -keystore camel-app.jks
keytool -import -trustcacerts -alias retail-app -file retail-app.crt -keystore client-app.jks
```

## Retail App Trusts Camel and Client Apps

### Import camel-app and client-app certificate into retail-app keystore:
```
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore retail-app.jks
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore retail-app.jks
```

## Camel and Client Apps Trust Investments App

### Import investment-app certificate into camel-app and client-app keystore:
```
keytool -import -trustcacerts -alias investment-app -file investment-app.crt -keystore camel-app.jks
keytool -import -trustcacerts -alias investment-app -file investment-app.crt -keystore client-app.jks
```

## Investments App Trusts Camel and Client Apps

### Import camel-app and client-app certificate into investment-app keystore:
```
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore investment-app.jks
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore investment-app.jks
```

## Client App Trusts Camel App

#### Import camel-app certificate into client-app keystore:
```
keytool -import -trustcacerts -alias camel-app -file camel-app.crt -keystore client-app.jks
```

## Camel App Trusts the Client App

##### Import client-app certificate into camel-app keystore:
```
keytool -import -trustcacerts -alias client-app -file client-app.crt -keystore camel-app.jks
```