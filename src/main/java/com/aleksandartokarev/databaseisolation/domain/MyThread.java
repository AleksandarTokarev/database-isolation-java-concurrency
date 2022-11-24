package com.aleksandartokarev.databaseisolation.domain;

import java.util.concurrent.Phaser;

public class MyThread implements Runnable {

    Phaser myPhaser;
    String threadName;

    public MyThread(Phaser myPhaser, String threadName) {
        this.myPhaser = myPhaser;
        this.threadName = threadName;
        myPhaser.register();
        new Thread(this).start();
    }

    @Override
    public void run() {
// phase 1 of our code.
        System.out.println("This is Phase one for : "+this.threadName);

// creating a phaser barrier for all threads to sync
        myPhaser.arriveAndAwaitAdvance();

        try {
            Thread.sleep(99);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// start new phase of execution, phase 2 of code
        System.out.println("This is Phase two for : "+this.threadName);

// creating a barrier for all threads to sync
        myPhaser.arriveAndAwaitAdvance();

        try {
            Thread.sleep(99);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// start new phase of execution, phase 3 of code
        System.out.println("This is Phase three for : "+this.threadName);

        myPhaser.arriveAndDeregister();

    }
}