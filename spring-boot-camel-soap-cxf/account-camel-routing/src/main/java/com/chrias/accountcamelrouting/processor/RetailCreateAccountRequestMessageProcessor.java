package com.chrias.accountcamelrouting.processor;

import com.chrias.camelsoapmodel.account.Account;
import com.chrias.soapmodel.retail.CreateAccountRequest;
import com.chrias.soapmodel.retail.RetailAccount;
import com.chrias.soapmodel.retail.RetailAccountType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class RetailCreateAccountRequestMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        Account accountToCreate = ((com.chrias.camelsoapmodel.account.CreateAccountRequest) payload.get(0)).getAccount();
        
        RetailAccount retailAccount = new RetailAccount();
        retailAccount.setCustomerId(accountToCreate.getCustomerId());
        retailAccount.setAccountType(RetailAccountType.fromValue(accountToCreate.getAccountType().toString()));
        retailAccount.setAccountNickname(accountToCreate.getAccountNickname());

        CreateAccountRequest retailRequest = new CreateAccountRequest();
        retailRequest.setAccount(retailAccount);
        
        exchange.getMessage().setBody(retailRequest);
    }

}
