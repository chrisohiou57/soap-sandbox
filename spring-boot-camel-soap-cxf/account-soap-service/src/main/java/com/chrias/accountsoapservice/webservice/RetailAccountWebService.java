package com.chrias.accountsoapservice.webservice;

import java.util.List;

import com.chrias.accountsoapservice.service.RetailAccountRetrievalService;
import com.chrias.soapmodel.retail.GetAccountsRequest;
import com.chrias.soapmodel.retail.GetAccountsResponse;
import com.chrias.soapmodel.retail.RetailAccount;
import com.chrias.soapmodel.retail.RetailAccountsPort;

import org.apache.cxf.feature.Features;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
public class RetailAccountWebService implements RetailAccountsPort {

    private RetailAccountRetrievalService retailAccountRetrievalService;

    public RetailAccountWebService(RetailAccountRetrievalService retailAccountRetrievalService) {
        this.retailAccountRetrievalService = retailAccountRetrievalService;
    }

    @Override
    public GetAccountsResponse getAccounts(GetAccountsRequest getAccountsRequest) {
        List<RetailAccount> accounts = retailAccountRetrievalService.retrieveAccountsByCustomerId(getAccountsRequest.getCustomerId());
        GetAccountsResponse response = new GetAccountsResponse();
        response.getAccount().addAll(accounts);
        return response;
    }
    
}
