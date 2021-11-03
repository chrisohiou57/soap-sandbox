package com.chrias.accountsoapservice.service;

import java.util.Arrays;
import java.util.List;

import com.chrias.soapmodel.investment.InvestmentAccount;
import com.chrias.soapmodel.investment.InvestmentAccountType;

public class InvestmentAccountRetrievalService {

    public List<InvestmentAccount> retrieveAccountsByCustomerId(String customerId) {
        InvestmentAccount checking = new InvestmentAccount();
        checking.setCustomerId(customerId);
        checking.setAccountNumber(customerId + "-40000");
        checking.setAccountNickname("My Roth IRA");
        checking.setAccountType(InvestmentAccountType.ROTH_IRA);

        InvestmentAccount savings = new InvestmentAccount();
        savings.setCustomerId(customerId);
        savings.setAccountNumber(customerId + "-50000");
        savings.setAccountNickname("My Traditional IRA");
        savings.setAccountType(InvestmentAccountType.TRADITIONAL_IRA);

        InvestmentAccount cd = new InvestmentAccount();
        cd.setCustomerId(customerId);
        cd.setAccountNumber(customerId + "-60000");
        cd.setAccountNickname("My Traditional 401k");
        cd.setAccountType(InvestmentAccountType.TRADITIONAL_401_K);

        return Arrays.asList(checking, savings, cd);
    }
    
}
