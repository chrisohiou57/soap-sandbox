# Overview
This repository is a reference implementation for utilizing the following technologies in conjunction:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Apache Camel](https://camel.apache.org/)
- [Apache CXF](https://cxf.apache.org/)
- [OpenTelemetry](https://opentelemetry.io/)

This project doesn't even come close to implementing all of the functionality available in these frameworks. However, it demonstrates some fundamental functionality that can be built upon. The fundamental features that this project aims to demonstrate are described further below.

## Features
The features implemented here are meant to demonstrate the use of these technologies in a Microservices architecture. In a microservices architecture applications typically communicate with one another using one of the strategies below:
- <b>Request Orchestration:</b> This approach requires an application to act as a coordinator for multiple API calls to one or more other microservices. The API calls are typically done over HTTP using the REST architectural style. However, the invoked services could be done in an RPC fashion via SOAP or a more modern RPC style such gRPC.
- <b>Events:</b> This approach involves a microservice emitting an event that one or more other microservices can consume. When a microservice emits an event the consumption of the event is done asynchronously. This means that other distributed applications can consume events at their own pace according to their processing power. Applications that emit events no longer need to block client calls that triggered the event while waiting for multiple synchronous responses. This approach promotes loose coupling and greater flexibility in evolution of distributed microservices by reducing dependencies on changes in API specs. Events are typically emitted using messaging or streaming technologies.
- <b>Hybrid</b> Sometimes it can be difficult to fully encompass all of the pertinent data in a single event. Certain domains have large and complex data structures that represent business activity. It can also be difficult to predict the details that will fulfil the needs of all your current and future event consumers. To remedy this, a hybrid approach can be taken where consumers can make API calls back to the event producer to get additional details that may not be present in the event.

### API Gateway
Enterprise APIs are deployed via API gateways. API gateways handle common tasks that are used across a system of API services, such as authentication, rate limiting, and statistics. In this project, Apache Camel is used as a SOAP and REST API gateway for our microservices.

Here is a good excerpt from the book <i>Microservices for [Java Developers, 2nd Edition by Rafael Benevides, Christian Posta](https://www.oreilly.com/library/view/microservices-for-java/9781492038290/)</i> that describes why an application such as this is beneficial.

> As the number of microservices grows, the complexity for the client who is consuming these APIs also grows.
> 
> Real applications could have dozens or even hundreds of microservices. A simple process like buying a book from an online store like Amazon can cause a client (your web browser or your mobile app) to use several other microservices. A client that has direct access to the microservice would have to locate and invoke them and handle any failures they caused itself. So, usually a better approach is to hide those services behind a new service layer. This aggregator service layer is known as an API gateway.
> 
> Another advantage of using an API gateway is that you can add cross-cutting concerns like authorization and data transformation in this layer. Services that use non-internet-friendly protocols can also benefit from the usage of an API gateway. However, keep in mind that it usually isn’t recommended to have a single API gateway for all the microservices in your application. If you (wrongly) decided to take that approach, it would act just like a monolithic bus, violating microservice independence by coupling all the microservices. Adding business logic to an API gateway is a mistake and should be avoided.

You may be asking, <i>"why bother with SOAP?"</i>. Supporting SOAP is still of interest to those who work in large enterprises. Many legacy applications were created when [SOA](https://en.wikipedia.org/wiki/Service-oriented_architecture) and [ESBs](https://en.wikipedia.org/wiki/Enterprise_service_bus) were all the rage. SOAP APIs were commonplace in these solutions.

It doesn't always make sense to migrate applications to the cloud in this type of environment. In some cases, an application could be near its end-of-life making it cost-ineffective to migrate. In other cases, applications may have deep ties to on-prem infrastructure and applications, such as a mainframe environment. The network ingress/egress costs, latency, and additional complexity incurred from bridging on-prem and cloud applications can sometimes be a limiting factor for successful migrations. Therefore, you may find yourself in a situation where you need to move away from legacy monolithic ESB infrastructure while maintaining support for SOAP integrations.

You can find additional details in the [gateway project](https://github.com/chrisohiou57/soap-sandbox/tree/main/spring-boot-camel-soap-cxf/account-camel-routing).

### SOAP API
TODO

### REST API
TODO

### Messaging
TODO

### Enterprise Integration Patterns (EIP)
TODO

# TODO (Next Steps)
- Document what is here in terms of apps, otel, observability
- Convert account-event-subscriber into Camel app and handle all async notifications
- POC Deployment Options:
    - EKS
        - Adapt [this workshop](https://www.eksworkshop.com/010_introduction/)
        - Documentation
        - [AWS otel](https://aws.amazon.com/otel/?otel-blogs.sort-by=item.additionalFields.createdDate&otel-blogs.sort-order=desc)
    - EC2
        - TomEE
        - Tomcat
        - WAS Liberty??
- Add additional connectivity POCs
    - Lambda to Camel HTTP Webhook
    - Invoke Lambda
    - AmazonMQ
    - SNS/SQS pub/sub
    - gRPC?
- Run in Camel K
    - Documentation
    - <b>What else?</b>