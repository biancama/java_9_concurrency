package com.biancama.exercise.java9concurrency.c01;

import static java.lang.String.format;
import static org.apache.commons.lang3.RandomUtils.nextLong;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 25/01/2019.
 */
public class E008ThreadLocalVariables {

    /**
     * Basically with ThreadLocal each thread has a NO shared variables
     *  when in a thread you start a  new thread => then localThread values are not shared
     *  if you want please use InheritableThreadLocal
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("#######  Unsafe Task  #########");
        UnsafeTask unsafeTask = new UnsafeTask();
        Thread[] unsafeThreads =  new Thread[10];
        Thread[] safeThreads =  new Thread[10];

        for (int i = 0; i < 10; i++) {
            unsafeThreads[i] = new Thread(unsafeTask);
            unsafeThreads[i].start();
            TimeUnit.SECONDS.sleep(nextLong(1, 3));
        }
        for (int i = 0; i < 10; i++) {
            unsafeThreads[i].join();
        }

        System.out.println("#######  Safe Task  #########");
        SafeTask safeTask = new SafeTask();

        for (int i = 0; i < 10; i++) {
            safeThreads[i] = new Thread(safeTask);
            safeThreads[i] .start();
            TimeUnit.SECONDS.sleep(nextLong(1, 3));
        }

        for (int i = 0; i < 10; i++) {
            safeThreads[i].join();
        }


    }

}


class UnsafeTask implements Runnable {
    private Date startDate;

    @Override
    public void run() {
        startDate = new Date();
        System.out.println(format("Starting thread: %s Start date; %s", Thread.currentThread().getName(), startDate));
        try {
            TimeUnit.SECONDS.sleep(nextLong(1, 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(format("Ending thread: %s Start date; %s", Thread.currentThread().getName(), startDate));
    }
}

class SafeTask implements Runnable {
    private ThreadLocal<Date> startDate;
    @Override
    public void run() {
        startDate = new ThreadLocal<Date>() {
            @Override
            protected Date initialValue() {
                return new Date();
            }
        };
        System.out.println(format("Starting thread: %s Start date; %s", Thread.currentThread().getName(), startDate.get()));
        try {
            TimeUnit.SECONDS.sleep(nextLong(1, 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(format("Ending thread: %s Start date; %s", Thread.currentThread().getName(), startDate.get()));
    }
}
