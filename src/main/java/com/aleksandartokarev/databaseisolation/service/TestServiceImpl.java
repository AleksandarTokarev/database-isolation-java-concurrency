package com.aleksandartokarev.databaseisolation.service;

import com.aleksandartokarev.databaseisolation.configuration.DatabaseCustomException;
import com.aleksandartokarev.databaseisolation.domain.TransferDTO;
import com.aleksandartokarev.databaseisolation.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    private Map<String, Integer> map = new HashMap<>();
    private Random randomGenerator = new Random();

    @Scheduled(fixedDelay = 100)
    public void refillMap() {
        for (int i = 0; i < 100; i++) {
            int j = randomGenerator.nextInt(100);
            map.put("Test" + j, j);
        }
//        System.out.println("END");
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transfer(TransferDTO transferDTO) throws DatabaseCustomException {
        Double fromBalance = testRepository.getBalance(transferDTO.getFrom());
        if (fromBalance >= transferDTO.getAmount()) {
            testRepository.addBalance(transferDTO.getFrom(), (-1) * transferDTO.getAmount());
            testRepository.addBalance(transferDTO.getTo(),  transferDTO.getAmount());
        }
    }

    @Override
    public Integer getMapValue(String key) throws DatabaseCustomException {
        try {
            return map.get(key);
        } catch(Exception e) {
            System.out.println(e.getClass() + ":" + e.getMessage());
            throw new DatabaseCustomException(DatabaseCustomException.Culprit.INTERNAL_ERROR, "Concurrent Error");
        }
    }
}
