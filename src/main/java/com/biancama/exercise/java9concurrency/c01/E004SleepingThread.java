package com.biancama.exercise.java9concurrency.c01;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 24/01/2019.
 */
public class E004SleepingThread {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new CounterClock());
        t.start();
        TimeUnit.SECONDS.sleep(5);
        t.interrupt();
    }
}

class CounterClock implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Clock " + new Date());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Thread Interrupted");
                //return;  if we don't return the thread won't stop
            }
        }
    }
}