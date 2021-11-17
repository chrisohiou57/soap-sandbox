package com.chrias.accounteventsubscriber.model;

public enum AccountType {

    TRADITIONAL_IRA("TRADITIONAL_IRA"),
    ROTH_IRA("ROTH_IRA"),
    TRADITIONAL_401_K("TRADITIONAL_401K"),
    CHECKING("CHECKING"),
    SAVINGS("SAVINGS"),
    CERTIFICATE_OF_DEPOSIT("CERTIFICATE_OF_DEPOSIT");
    private final String value;

    AccountType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccountType fromValue(String v) {
        for (AccountType c: AccountType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
}
