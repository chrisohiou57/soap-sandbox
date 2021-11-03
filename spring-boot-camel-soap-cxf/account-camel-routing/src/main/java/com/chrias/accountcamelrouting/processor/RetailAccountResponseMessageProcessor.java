package com.chrias.accountcamelrouting.processor;

import com.chrias.camelsoapmodel.account.Account;
import com.chrias.camelsoapmodel.account.AccountType;
import com.chrias.camelsoapmodel.account.GetAccountsResponse;
import com.chrias.soapmodel.retail.RetailAccount;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;

public class RetailAccountResponseMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        com.chrias.soapmodel.retail.GetAccountsResponse retailResponse = ((com.chrias.soapmodel.retail.GetAccountsResponse) payload.get(0));

        GetAccountsResponse response = new GetAccountsResponse();
        retailResponse.getAccount().forEach(ra -> {
            RetailAccount retailAccount = (RetailAccount) ra;
            Account account = new Account();
            account.setCustomerId(retailAccount.getCustomerId());
            account.setAccountNumber(retailAccount.getAccountNumber());
            account.setAccountNickname(retailAccount.getAccountNickname());
            account.setAccountType(AccountType.fromValue(retailAccount.getAccountType().toString()));
            response.getAccount().add(account);
        });
        exchange.getMessage().setBody(response);
    }
    
}
