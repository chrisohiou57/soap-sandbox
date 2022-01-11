package com.chrias.accountcamelrouting.config;

import org.apache.camel.component.amqp.AMQPConnectionDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Value("${aws.mq.broker}")
    private String awsMqBroker;

    @Value("${aws.mq.username}")
    private String awsMqUsername;

    @Value("${aws.mq.password}")
    private String awsMqPassword;

    @Bean
    AMQPConnectionDetails securedAmqpConnection() {
        return new AMQPConnectionDetails(String.format("amqps://%s:5671", awsMqBroker), awsMqUsername, awsMqPassword);
    }
    
}
