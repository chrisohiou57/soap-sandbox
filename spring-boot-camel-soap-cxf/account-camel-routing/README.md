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
Hawtio is a pluggable management console for Java stuff which supports any kind of JVM, any kind of container (Tomcat, Jetty, Spring Boot, etc), and any kind of Java technology and middleware. This gateway project is configured to use Hawtio with the [camel management features](https://github.com/hawtio/hawtio-integration/blob/master/plugins/camel/doc/help.md). You can 