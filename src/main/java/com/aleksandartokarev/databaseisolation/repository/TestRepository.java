package com.aleksandartokarev.databaseisolation.repository;

public interface TestRepository {
    void addBalance(Integer iban, Double amount);

    Double getBalance(Integer iban);
}
