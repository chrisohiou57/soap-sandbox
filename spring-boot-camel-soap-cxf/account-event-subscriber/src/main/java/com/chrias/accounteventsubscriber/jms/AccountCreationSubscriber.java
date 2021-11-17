package com.chrias.accounteventsubscriber.jms;

import com.chrias.accounteventsubscriber.aop.GlobalEnvSpanTagger;
import com.chrias.accounteventsubscriber.model.Account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class AccountCreationSubscriber {

    Logger log = LoggerFactory.getLogger(AccountCreationSubscriber.class);

    @GlobalEnvSpanTagger
    @JmsListener(destination = "accountCreation", containerFactory = "jsonListenerContainerFactory")
    public void handleAccountCreation(Account account) {
        try {
            log.info("Received Account Creation Message: " + account);
            log.info("Simulating long running process...");
            Thread.sleep(10000L);
        } catch (Exception e) {
            log.error("An error occurred while trying to sleep", e);
        }
    }
    
}
