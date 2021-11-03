package com.chrias.accountcamelrouting.bean;

import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvestmentAccountRequestMessageConverter {

    private static Logger log = LoggerFactory.getLogger(InvestmentAccountRequestMessageConverter.class);

    public void convertRequestMessage(Exchange exchange) {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        String customerId = ((com.chrias.camelsoapmodel.account.GetAccountsRequest) payload.get(0)).getCustomerId();
        log.debug("Creating investment account search request for customer ID: {}", customerId);
        
        com.chrias.soapmodel.investment.GetAccountsRequest investmentGetAccountsRequest = new com.chrias.soapmodel.investment.GetAccountsRequest();
        investmentGetAccountsRequest.setCustomerId(customerId);
        
        exchange.getMessage().setBody(investmentGetAccountsRequest);
    }
    
}
