package com.chrias.accountsoapservice.service;

import java.util.Arrays;
import java.util.List;

import com.chrias.soapmodel.retail.RetailAccount;
import com.chrias.soapmodel.retail.RetailAccountType;

public class RetailAccountRetrievalService {

    public List<RetailAccount> retrieveAccountsByCustomerId(String customerId) {
        RetailAccount checking = new RetailAccount();
        checking.setCustomerId(customerId);
        checking.setAccountNumber(customerId + "-10000");
        checking.setAccountNickname("My Checking");
        checking.setAccountType(RetailAccountType.CHECKING);

        RetailAccount savings = new RetailAccount();
        savings.setCustomerId(customerId);
        savings.setAccountNumber(customerId + "-20000");
        savings.setAccountNickname("My Savings");
        savings.setAccountType(RetailAccountType.SAVINGS);

        RetailAccount cd = new RetailAccount();
        cd.setCustomerId(customerId);
        cd.setAccountNumber(customerId + "-30000");
        cd.setAccountNickname("My CD");
        cd.setAccountType(RetailAccountType.CERTIFICATE_OF_DEPOSIT);

        return Arrays.asList(checking, savings, cd);
    }
    
}
