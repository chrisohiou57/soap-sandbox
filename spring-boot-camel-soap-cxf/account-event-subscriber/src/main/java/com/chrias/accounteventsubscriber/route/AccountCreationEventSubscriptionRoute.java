package com.chrias.accounteventsubscriber.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationEventSubscriptionRoute extends RouteBuilder {

    @Value("${jms.queue.account.creation}")
    private String accountCreationQueue;

    @Value("${sqs.account.creation.arn}")
    private Object sqsAccountCreationArn;

    @Value("${s3.account.creation.arn}")
    private String s3AccountCreationArn;

    @Override
    public void configure() throws Exception {

        from(String.format("jms:%s?jmsMessageType=Text", accountCreationQueue)).routeId("subscribe-acct-creation-artemis")
            .log("Received Artemis account creation notification: ${body}")
        .end();

        from(String.format("aws2-sqs://%s:%s", sqsAccountCreationArn, accountCreationQueue)).routeId("subscribe-acct-creation-aws-sqs")
            .log("Received AWS SQS account creation notification: ${body}")
        .end();

        // from(String.format("amqp:%s?jmsMessageType=Text", accountCreationQueue)).routeId("subscribe-acct-creation-aws-mq")
        //     .log("Received AmazonMQ account creation notification: ${body}")
        // .end();

        from(String.format("aws2-s3://%s?moveAfterRead=true&destinationBucket=acct-demo-account-creation-processed", s3AccountCreationArn))
            .log("Received AWS S3 account creation file: ${body}")
        .end();
        
    }

}
