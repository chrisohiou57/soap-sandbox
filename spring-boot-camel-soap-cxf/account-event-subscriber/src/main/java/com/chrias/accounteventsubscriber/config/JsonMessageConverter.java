package com.chrias.accounteventsubscriber.config;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.chrias.accounteventsubscriber.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class JsonMessageConverter implements MessageConverter {

    private final ObjectMapper mapper;

    public JsonMessageConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        try {
            return session.createTextMessage(object.getClass().getName() + "=" + mapper.writeValueAsString(object));
        } catch (Exception e) {
            throw new MessageConversionException("Message cannot be serialized", e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            String camelOperation = message.getStringProperty("operationName");
            String text = ((TextMessage) message).getText();

            switch (camelOperation) {
                case "createAccount":
                    return mapper.readValue(text, Account.class);
                default:
                    throw new RuntimeException("Unknown Camel operation encountered");
            }
        } catch (Exception e) {
            throw new MessageConversionException("Message cannot be deserialized", e);
        }
    }
    
}
