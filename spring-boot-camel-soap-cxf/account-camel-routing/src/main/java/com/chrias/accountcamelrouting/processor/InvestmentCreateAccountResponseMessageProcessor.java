package com.chrias.accountcamelrouting.processor;

import com.chrias.camelsoapmodel.account.Account;
import com.chrias.camelsoapmodel.account.AccountType;
import com.chrias.camelsoapmodel.account.CreateAccountResponse;
import com.chrias.soapmodel.investment.InvestmentAccount;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class InvestmentCreateAccountResponseMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        InvestmentAccount investmentAccount = ((com.chrias.soapmodel.investment.CreateAccountResponse) payload.get(0)).getAccount();

        Account account = new Account();
        account.setCustomerId(investmentAccount.getCustomerId());
        account.setAccountType(AccountType.fromValue(investmentAccount.getAccountType().toString()));
        account.setAccountNumber(investmentAccount.getAccountNumber());
        account.setAccountNickname(investmentAccount.getAccountNickname());

        CreateAccountResponse response = new CreateAccountResponse();
        response.setAccount(account);

        exchange.getMessage().setBody(response);
    }
    
}
