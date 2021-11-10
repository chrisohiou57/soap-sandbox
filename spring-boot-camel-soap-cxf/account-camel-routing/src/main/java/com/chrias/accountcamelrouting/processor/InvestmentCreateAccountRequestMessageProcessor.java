package com.chrias.accountcamelrouting.processor;

import com.chrias.camelsoapmodel.account.Account;
import com.chrias.soapmodel.investment.CreateAccountRequest;
import com.chrias.soapmodel.investment.InvestmentAccount;
import com.chrias.soapmodel.investment.InvestmentAccountType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class InvestmentCreateAccountRequestMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        Account accountToCreate = ((com.chrias.camelsoapmodel.account.CreateAccountRequest) payload.get(0)).getAccount();
        
        InvestmentAccount investmentAccount = new InvestmentAccount();
        investmentAccount.setCustomerId(accountToCreate.getCustomerId());
        investmentAccount.setAccountType(InvestmentAccountType.fromValue(accountToCreate.getAccountType().toString()));
        investmentAccount.setAccountNickname(accountToCreate.getAccountNickname());

        CreateAccountRequest retailRequest = new CreateAccountRequest();
        retailRequest.setAccount(investmentAccount);
        
        exchange.getMessage().setBody(retailRequest);
    }
    
}
