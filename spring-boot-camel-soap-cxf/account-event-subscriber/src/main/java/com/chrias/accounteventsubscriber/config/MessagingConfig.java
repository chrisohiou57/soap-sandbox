package com.chrias.accounteventsubscriber.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class MessagingConfig {

    @Bean(name="jsonListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jsonListenerContainerFactory(ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            @Qualifier("jsonMessageConverter") MessageConverter jsonMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        
        // This provides all boot's default to this factory
        configurer.configure(factory, connectionFactory);

        // You could still override other Boot defaults if necessary
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
    
}
