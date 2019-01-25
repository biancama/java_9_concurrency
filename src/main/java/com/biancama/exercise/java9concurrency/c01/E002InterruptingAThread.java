package com.biancama.exercise.java9concurrency.c01;

import static java.lang.String.format;
import static org.apache.commons.lang3.RandomUtils.nextLong;

import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 23/01/2019.
 */
public class E002InterruptingAThread {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new PrimeNumber());
        t.start();
        TimeUnit.SECONDS.sleep(nextLong(1, 10));

        // interrupt the thread
        t.interrupt();
        t.join();
        System.out.println("Main thread is finished !!!");
    }



}
class PrimeNumber implements Runnable {

    @Override
    public void run() {
        long number = 1L;
        while (true) {
            if (isPrime(number)) {
                System.out.println(format("Number %d is prime", number));
            }
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("The prime generator has been interrupted");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long b) {
        if (b <= 2) return true;
        else if (b % 2 == 0) return false;
        else {
            for (int i = 2; i < b ; i++) {
                if (b % i == 0) return false;
            }
            return true;
        }
    }
}