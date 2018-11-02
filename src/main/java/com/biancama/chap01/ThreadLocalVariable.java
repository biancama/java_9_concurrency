package com.biancama.chap01;

import static org.apache.commons.lang3.RandomUtils.nextLong;

import java.util.Date;
import java.util.concurrent.TimeUnit;
/**
 * Created by massimo.biancalani on 03/08/2017.
 */
public class ThreadLocalVariable {
    public static void main(String[] args) {
        UnsafeTask unsafeTask = new UnsafeTask();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(unsafeTask);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SafeTask safeTask = new SafeTask();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(safeTask);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


class UnsafeTask implements Runnable {
    private Date startDate;

    @Override
    public void run() {
        startDate = new Date();
        System.out.printf("Starting Thread: %s : %s\n",
            Thread.currentThread().getId(), startDate);
        try {
            TimeUnit.SECONDS.sleep(nextLong(10, 13));

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n",
            Thread.currentThread().getId(), startDate);
    }
}

class SafeTask implements Runnable {

    private static ThreadLocal<Date>  startDate = ThreadLocal.withInitial(()-> new Date());

    @Override
    public void run() {
        System.out.printf("Starting Thread: %s : %s\n",
            Thread.currentThread().getId(), startDate.get());
        try {
            TimeUnit.SECONDS.sleep(nextLong(10, 13));

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n",
            Thread.currentThread().getId(), startDate.get());
    }
}