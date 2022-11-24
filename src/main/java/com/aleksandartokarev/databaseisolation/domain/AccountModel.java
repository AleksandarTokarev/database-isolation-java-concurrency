package com.aleksandartokarev.databaseisolation.domain;

public class AccountModel {
    private Integer iban;
    private Double balance;

    public Integer getIban() {
        return iban;
    }

    public void setIban(Integer iban) {
        this.iban = iban;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
