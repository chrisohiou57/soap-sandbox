package com.chrias.accounteventsubscriber.model;

public class Account {
    
    private String customerId;
    private String accountNumber;
    private String accountNickname;
    protected AccountType accountType;

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNickname() {
        return this.accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    @Override
    public String toString() {
        return "{" +
            " customerId='" + getCustomerId() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", accountNickname='" + getAccountNickname() + "'" +
            ", accountType='" + getAccountType() + "'" +
            "}";
    }

}
