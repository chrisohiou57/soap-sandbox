package com.chrias.accountsoapservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.chrias.soapmodel.retail.RetailAccount;
import com.chrias.soapmodel.retail.RetailAccountType;

public class RetailAccountService {

    private List<RetailAccount> accounts;

    public RetailAccountService() {
        this.accounts = new CopyOnWriteArrayList<>();

        RetailAccount checking = new RetailAccount();
        checking.setCustomerId("1");
        checking.setAccountNumber("1-10000");
        checking.setAccountNickname("My Checking");
        checking.setAccountType(RetailAccountType.CHECKING);

        RetailAccount savings = new RetailAccount();
        savings.setCustomerId("1");
        savings.setAccountNumber("1-20000");
        savings.setAccountNickname("My Savings");
        savings.setAccountType(RetailAccountType.SAVINGS);

        RetailAccount cd = new RetailAccount();
        cd.setCustomerId("1");
        cd.setAccountNumber("1-30000");
        cd.setAccountNickname("My CD");
        cd.setAccountType(RetailAccountType.CERTIFICATE_OF_DEPOSIT);

        this.accounts.addAll(Arrays.asList(checking, savings, cd));
    }

    public List<RetailAccount> retrieveAccountsByCustomerId(String customerId) {
        return this.accounts
            .stream()
            .filter(acct -> acct.getCustomerId().equalsIgnoreCase(customerId))
            .collect(Collectors.toList());
    }

    public RetailAccount createAccount(RetailAccount account) {
        String customerId = account.getCustomerId();
        List<RetailAccount> existingAccounts = retrieveAccountsByCustomerId(customerId);

        if (existingAccounts.isEmpty()) {
            account.setAccountNumber(String.format("%s-10000", customerId));
        } else {
            account.setAccountNumber(String.format("%s-R-%d0000", customerId, existingAccounts.size() + 1));
        }

        this.accounts.add(account);
        return account;
    }
    
}
