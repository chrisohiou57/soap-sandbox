package com.chrias.accountcamelrouting.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationSubscriberRoute extends RouteBuilder {

    @Value("${jms.queue.account.creation}")
    private String accountCreationQueue;

    @Override
    public void configure() throws Exception {
        from("aws2-sqs://arn:aws:sqs:us-east-2:621297402434:accountCreation")
        .log("Received SQS account creation notification: ${body}")
        .end();
    }
    
}
