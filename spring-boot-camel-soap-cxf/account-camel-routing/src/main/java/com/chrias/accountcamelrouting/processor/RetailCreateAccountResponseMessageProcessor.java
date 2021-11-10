package com.chrias.accountcamelrouting.processor;

import com.chrias.camelsoapmodel.account.Account;
import com.chrias.camelsoapmodel.account.AccountType;
import com.chrias.camelsoapmodel.account.CreateAccountResponse;
import com.chrias.soapmodel.retail.RetailAccount;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class RetailCreateAccountResponseMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        RetailAccount retailAccount = ((com.chrias.soapmodel.retail.CreateAccountResponse) payload.get(0)).getAccount();

        Account account = new Account();
        account.setCustomerId(retailAccount.getCustomerId());
        account.setAccountType(AccountType.fromValue(retailAccount.getAccountType().toString()));
        account.setAccountNumber(retailAccount.getAccountNumber());
        account.setAccountNickname(retailAccount.getAccountNickname());

        CreateAccountResponse response = new CreateAccountResponse();
        response.setAccount(account);

        exchange.getMessage().setBody(response);
    }
    
}
