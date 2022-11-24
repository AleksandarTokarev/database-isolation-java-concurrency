package com.aleksandartokarev.databaseisolation.controller;

import com.aleksandartokarev.databaseisolation.configuration.DatabaseCustomException;
import com.aleksandartokarev.databaseisolation.domain.MyThread;
import com.aleksandartokarev.databaseisolation.domain.TransferDTO;
import com.aleksandartokarev.databaseisolation.repository.TestRepository;
import com.aleksandartokarev.databaseisolation.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public String transferCountdownLatch(@RequestBody TransferDTO transferDTO) throws DatabaseCustomException, InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);

        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();

                    testService.transfer(new TransferDTO(transferDTO.getFrom(), transferDTO.getTo(), transferDTO.getAmount()));
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
        return "OK";
    }

    @RequestMapping(value="/transfer", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String transfer(@RequestBody TransferDTO transferDTO) throws DatabaseCustomException {
        testService.transfer(new TransferDTO(transferDTO.getFrom(), transferDTO.getTo(), transferDTO.getAmount()));
        return "OK";
    }


    @RequestMapping(value="/getMapValue", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getMapValue(@RequestParam String mapKey) throws DatabaseCustomException {
        return testService.getMapValue(mapKey);
    }


    @RequestMapping(value="/testMapValues", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void testMapValues() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        barrier.await();
                        System.out.println(testService.getMapValue("Test5"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    } catch (DatabaseCustomException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(1000); // not needed - just put there in case we want to do something
    }


    @RequestMapping(value="/concurrentDataStructures", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void concurrentDataStructures() throws InterruptedException, ConcurrentModificationException {
        // Concurrent Modification on List
//        List<Integer> list = new ArrayList<>();
        Queue<Integer> list = new ConcurrentLinkedQueue<>();

//        List<Integer> list = ConcurrentLinkedQueueollections.synchronizedList(new ArrayList<Integer>());
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            Integer value = it.next();
            System.out.println("List Value:" + value);
            if (value.equals(3))
                list.remove(value);
        }
        // Concurrent Modifications on Map
//        Map<Integer, Integer> map = new HashMap<>();
        Map<Integer, Integer> map = new ConcurrentHashMap<>();
//        Map<Integer,Integer> map = Collections.synchronizedMap(new HashMap<>()); // same problem as normal hashmap
        map.put(1, 1);
        map.put(2, 2);
        map.put(3,3);

        Iterator<Integer> it2 = map.keySet().iterator();
        while(it2.hasNext()) {
            Integer key = it2.next();
            System.out.println("Map Value:" + map.get(key));
            if (key.equals(2)) {
                map.put(2, 4);
                map.put(5, 4);
            }
        }
        System.out.println("DONE");
    }


    @RequestMapping(value="/phaser", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void phaser() throws InterruptedException, ConcurrentModificationException {
        Phaser myPhaser = new Phaser();
        myPhaser.register();

        System.out.println("let's start phaser example");

        int phase=0;

        MyThread cat = new MyThread(myPhaser, "cat");
        MyThread dog = new MyThread(myPhaser, "dog");
        MyThread elephant = new MyThread(myPhaser, "elephant");

        myPhaser.arriveAndAwaitAdvance();

        System.out.println("Ending phase one");

        myPhaser.arriveAndAwaitAdvance();

        System.out.println("Ending phase two");

        myPhaser.arriveAndAwaitAdvance();

        System.out.println("Ending phase three");
    }
}
