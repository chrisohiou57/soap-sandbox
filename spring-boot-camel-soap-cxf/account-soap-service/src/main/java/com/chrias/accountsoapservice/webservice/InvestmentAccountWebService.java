package com.chrias.accountsoapservice.webservice;

import java.util.List;

import com.chrias.accountsoapservice.service.InvestmentAccountService;
import com.chrias.soapmodel.investment.CreateAccountRequest;
import com.chrias.soapmodel.investment.CreateAccountResponse;
import com.chrias.soapmodel.investment.GetAccountsRequest;
import com.chrias.soapmodel.investment.GetAccountsResponse;
import com.chrias.soapmodel.investment.InvestmentAccount;
import com.chrias.soapmodel.investment.InvestmentAccountsPort;

public class InvestmentAccountWebService implements InvestmentAccountsPort {

    private InvestmentAccountService investmentAccountService;

    public InvestmentAccountWebService(InvestmentAccountService investmentAccountRetrievalService) {
        this.investmentAccountService = investmentAccountRetrievalService;
    }

    @Override
    public GetAccountsResponse getAccounts(GetAccountsRequest getAccountsRequest) {
        List<InvestmentAccount> accounts = investmentAccountService.retrieveAccountsByCustomerId(getAccountsRequest.getCustomerId());
        GetAccountsResponse response = new GetAccountsResponse();
        response.getAccount().addAll(accounts);
        return response;
    }

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest) {
        InvestmentAccount account = investmentAccountService.createAccount(createAccountRequest.getAccount());
        CreateAccountResponse response = new CreateAccountResponse();
        response.setAccount(account);
        return response;
    }
    
}
