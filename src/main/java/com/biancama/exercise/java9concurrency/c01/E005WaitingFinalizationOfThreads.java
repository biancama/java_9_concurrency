package com.biancama.exercise.java9concurrency.c01;

import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 24/01/2019.
 */
public class E005WaitingFinalizationOfThreads {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main started");

        Thread t1 = new Thread(new DataSourcesLoader());
        Thread t2 = new Thread(new NetworkLoader());
        t1.start();
        t2.start();

        t2.join();  // the order does not matter
        t1.join();

        System.out.println("Main ended");

    }
}

class DataSourcesLoader implements Runnable {

    @Override
    public void run() {
        System.out.println("Thread Data Sources started");

        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread Data Sources ended");
    }
}
class NetworkLoader implements Runnable {

@Override
public void run() {
    System.out.println("Thread NetworkLoader started");

    try {
        TimeUnit.SECONDS.sleep(6);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
     System.out.println("Thread NetworkLoader ended");
    }
}