package com.chrias.accountcamelrouting.processor;

import com.chrias.soapmodel.retail.GetAccountsRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetailAccountRequestMessageProcessor implements Processor {

    private static Logger log = LoggerFactory.getLogger(RetailAccountRequestMessageProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        String customerId = ((com.chrias.camelsoapmodel.account.GetAccountsRequest) payload.get(0)).getCustomerId();
        log.debug("Creating retail account search request for customer ID: {}", customerId);
        
        GetAccountsRequest retailGetAccountsRequest = new GetAccountsRequest();
        retailGetAccountsRequest.setCustomerId(customerId);
        
        exchange.getMessage().setBody(retailGetAccountsRequest);
    }
    
}
