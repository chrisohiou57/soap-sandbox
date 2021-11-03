package com.chrias.accountcamelrouting.bean;

import com.chrias.camelsoapmodel.account.Account;
import com.chrias.camelsoapmodel.account.AccountType;
import com.chrias.camelsoapmodel.account.GetAccountsResponse;
import com.chrias.soapmodel.investment.InvestmentAccount;

import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;

public class RetailAccountResponseMessageConverter {

    public void convertResponseMessage(Exchange exchange) {
        MessageContentsList payload = exchange.getIn().getBody(MessageContentsList.class);
        com.chrias.soapmodel.investment.GetAccountsResponse retailResponse = ((com.chrias.soapmodel.investment.GetAccountsResponse) payload.get(0));

        GetAccountsResponse response = new GetAccountsResponse();
        retailResponse.getAccount().forEach(ia -> {
            InvestmentAccount investmentAccount = (InvestmentAccount) ia;
            Account account = new Account();
            account.setCustomerId(investmentAccount.getCustomerId());
            account.setAccountNumber(investmentAccount.getAccountNumber());
            account.setAccountNickname(investmentAccount.getAccountNickname());
            account.setAccountType(AccountType.fromValue(investmentAccount.getAccountType().value()));
            response.getAccount().add(account);
        });
        exchange.getMessage().setBody(response);
    }
    
}
