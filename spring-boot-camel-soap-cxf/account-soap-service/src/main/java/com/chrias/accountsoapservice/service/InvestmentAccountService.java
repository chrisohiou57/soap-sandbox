package com.chrias.accountsoapservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.chrias.soapmodel.investment.InvestmentAccount;
import com.chrias.soapmodel.investment.InvestmentAccountType;

public class InvestmentAccountService {

    private List<InvestmentAccount> accounts;

    public InvestmentAccountService() {
        this.accounts = new CopyOnWriteArrayList<>();

        InvestmentAccount rothIra = new InvestmentAccount();
        rothIra.setCustomerId("1");
        rothIra.setAccountNumber("1-40000");
        rothIra.setAccountNickname("My Roth IRA");
        rothIra.setAccountType(InvestmentAccountType.ROTH_IRA);

        InvestmentAccount traditionIra = new InvestmentAccount();
        traditionIra.setCustomerId("1");
        traditionIra.setAccountNumber("1-50000");
        traditionIra.setAccountNickname("My Traditional IRA");
        traditionIra.setAccountType(InvestmentAccountType.TRADITIONAL_IRA);

        InvestmentAccount tradtional401k = new InvestmentAccount();
        tradtional401k.setCustomerId("1");
        tradtional401k.setAccountNumber("1-60000");
        tradtional401k.setAccountNickname("My Traditional 401k");
        tradtional401k.setAccountType(InvestmentAccountType.TRADITIONAL_401_K);

        this.accounts.addAll(Arrays.asList(rothIra, traditionIra, tradtional401k));
    }

    public List<InvestmentAccount> retrieveAccountsByCustomerId(String customerId) {
        return this.accounts
            .stream()
            .filter(acct -> acct.getCustomerId().equalsIgnoreCase(customerId))
            .collect(Collectors.toList());
    }

    public InvestmentAccount createAccount(InvestmentAccount account) {
        String customerId = account.getCustomerId();
        List<InvestmentAccount> existingAccounts = retrieveAccountsByCustomerId(customerId);

        if (existingAccounts.isEmpty()) {
            account.setAccountNumber(String.format("%s-10000", customerId));
        } else {
            account.setAccountNumber(String.format("%s-I-%d0000", customerId, existingAccounts.size() + 1));
        }

        this.accounts.add(account);
        return account;
    }
    
}
