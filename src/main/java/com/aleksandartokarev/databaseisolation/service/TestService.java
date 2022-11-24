package com.aleksandartokarev.databaseisolation.service;

import com.aleksandartokarev.databaseisolation.configuration.DatabaseCustomException;
import com.aleksandartokarev.databaseisolation.domain.TransferDTO;

public interface TestService {
    void transfer(TransferDTO transferDTO) throws DatabaseCustomException;

    Integer getMapValue(String key) throws DatabaseCustomException;
}
