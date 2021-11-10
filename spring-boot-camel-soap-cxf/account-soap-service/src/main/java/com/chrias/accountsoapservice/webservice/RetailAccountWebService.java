package com.chrias.accountsoapservice.webservice;

import java.util.List;

import com.chrias.accountsoapservice.service.RetailAccountService;
import com.chrias.soapmodel.retail.CreateAccountRequest;
import com.chrias.soapmodel.retail.CreateAccountResponse;
import com.chrias.soapmodel.retail.GetAccountsRequest;
import com.chrias.soapmodel.retail.GetAccountsResponse;
import com.chrias.soapmodel.retail.RetailAccount;
import com.chrias.soapmodel.retail.RetailAccountsPort;

import org.apache.cxf.feature.Features;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
public class RetailAccountWebService implements RetailAccountsPort {

    private RetailAccountService retailAccountService;

    public RetailAccountWebService(RetailAccountService retailAccountService) {
        this.retailAccountService = retailAccountService;
    }

    @Override
    public GetAccountsResponse getAccounts(GetAccountsRequest getAccountsRequest) {
        List<RetailAccount> accounts = retailAccountService.retrieveAccountsByCustomerId(getAccountsRequest.getCustomerId());
        GetAccountsResponse response = new GetAccountsResponse();
        response.getAccount().addAll(accounts);
        return response;
    }

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest) {
        RetailAccount account = retailAccountService.createAccount(createAccountRequest.getAccount());
        CreateAccountResponse response = new CreateAccountResponse();
        response.setAccount(account);
        return response;
    }
    
}
