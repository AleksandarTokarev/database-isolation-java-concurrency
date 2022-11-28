package com.aleksandartokarev.databaseisolation.controller;

import com.aleksandartokarev.databaseisolation.configuration.DatabaseCustomException;
import com.aleksandartokarev.databaseisolation.domain.MyThread;
import com.aleksandartokarev.databaseisolation.domain.TransferDTO;
import com.aleksandartokarev.databaseisolation.repository.TestRepository;
import com.aleksandartokarev.databaseisolation.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private TestRepository testRepository;

    int threadCount = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value="/transferCountdownLatch", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void transferCountdownLatch(@RequestBody TransferDTO transferDTO) throws DatabaseCustomException, InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);

        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();

                    testService.transfer(transferDTO);
                } catch (Exception e) {
                    LOGGER.error("Transfer failed", e);
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        startLatch.countDown();
        endLatch.await();

        LOGGER.info(
                "Alice's balance {}",
                testRepository.getBalance(1)
        );
        LOGGER.info(
                "Bob's balance {}",
                testRepository.getBalance(2)
        );
    }

    @RequestMapping(value="/transferCyclicBarrier", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void transferCyclicBarrier(@RequestBody TransferDTO transferDTO) throws DatabaseCustomException, InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    testService.transfer(transferDTO);
                } catch (Exception e) {
                    LOGGER.error("Transfer failed", e);
                }
            }).start();
        }
    }

    @RequestMapping(value="/transfer", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@RequestBody TransferDTO transferDTO) throws DatabaseCustomException {
        testService.transfer(transferDTO);
    }

    @RequestMapping(value="/phaser", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void phaser() throws InterruptedException, ConcurrentModificationException {
        Phaser myPhaser = new Phaser();
        myPhaser.register();
        System.out.println("let's start phaser example");

        MyThread firstThread = new MyThread(myPhaser, "firstThread");
        MyThread secondThread = new MyThread(myPhaser, "secondThread");
        MyThread thirdThread = new MyThread(myPhaser, "thirdThread");

        myPhaser.arriveAndAwaitAdvance();
        System.out.println("Ending phase one");

        myPhaser.arriveAndAwaitAdvance();
        System.out.println("Ending phase two");

        myPhaser.arriveAndAwaitAdvance();
        System.out.println("Ending phase three");
    }

    @RequestMapping(value="/concurrentDataStructuresMap", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void concurrentDataStructuresMap() throws InterruptedException, ConcurrentModificationException {
//        Map<Integer, Integer> map = new HashMap<>();
        Map<Integer, Integer> map = new ConcurrentHashMap<>();
//        Map<Integer,Integer> map = Collections.synchronizedMap(new HashMap<>()); // same problem as normal hashmap
        map.put(1, 1);
        map.put(2, 2);
        map.put(3,3);
        try {
            Iterator<Integer> it2 = map.keySet().iterator();
            while (it2.hasNext()) {
                Integer key = it2.next();
                System.out.println("Map Value:" + map.get(key));
                if (key.equals(2)) {
                    map.put(2, 4);
                    map.put(5, 4);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getClass() + ":" + e.getMessage());
        }
    }

    @RequestMapping(value="/concurrentDataStructuresList", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void concurrentDataStructures() throws InterruptedException, ConcurrentModificationException {
        // Concurrent Modification on List
//        List<Integer> list = new ArrayList<>();
//        Queue<Integer> list = new ConcurrentLinkedQueue<>();
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
//        List<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        try {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                Integer value = it.next();
                System.out.println("List Value:" + value);
                if (value.equals(3))
                    list.remove(value);
            }
        } catch (Exception e) {
            System.out.println(e.getClass() + ":" + e.getMessage());
        }
    }

    @RequestMapping(value="/getMapValue", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getMapValue(@RequestParam(defaultValue = "Test6") String mapKey) throws DatabaseCustomException {
        return testService.getMapValue(mapKey);
    }

    @RequestMapping(value="/getMapValueWithBarrier", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void getMapValueWithBarrier(@RequestParam String mapKey) throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        System.out.println(testService.getMapValue(mapKey));
                    } catch (Exception e) {
                        System.out.println(e.getClass() + ":" + e.getMessage());
                    }
                }
            }).start();
        }
    }


}
