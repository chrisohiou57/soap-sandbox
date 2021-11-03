package com.chrias.accountsoapservice.webservice;

import java.util.List;

import com.chrias.accountsoapservice.service.InvestmentAccountRetrievalService;
import com.chrias.soapmodel.investment.GetAccountsRequest;
import com.chrias.soapmodel.investment.GetAccountsResponse;
import com.chrias.soapmodel.investment.InvestmentAccount;
import com.chrias.soapmodel.investment.InvestmentAccountsPort;

public class InvestmentAccountWebService implements InvestmentAccountsPort {

    private InvestmentAccountRetrievalService investmentAccountRetrievalService;

    public InvestmentAccountWebService(InvestmentAccountRetrievalService investmentAccountRetrievalService) {
        this.investmentAccountRetrievalService = investmentAccountRetrievalService;
    }

    @Override
    public GetAccountsResponse getAccounts(GetAccountsRequest getAccountsRequest) {
        List<InvestmentAccount> accounts = investmentAccountRetrievalService.retrieveAccountsByCustomerId(getAccountsRequest.getCustomerId());
        GetAccountsResponse response = new GetAccountsResponse();
        response.getAccount().addAll(accounts);
        return response;
    }
    
}
