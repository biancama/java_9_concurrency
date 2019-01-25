package com.biancama.exercise.java9concurrency.c01;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;

/**
 * Created by massimo.biancalani on 22/01/2019.
 */
public class E001CreatingRunningSettingCharacteristics {

    /* create 10 threads calculates the prime numbers minor 20000 numbers */
    public static void main(String[] args) {
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];
        Thread.State[] threadsStates = new Thread.State[numberOfThreads];
        // Create threads
        for (int i= 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new PrimeNumberRunnable(), format("Thread num: %d", i));
            if (i %2 == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
        }
        getState(threadsStates, threads);
        // start threads
        for (int i= 0; i < numberOfThreads; i++) {
            threads[i].start();
        }
        getState(threadsStates, threads);

        // waiting for the end
        while (!isFinish(threads, threadsStates)) ;
        System.out.println("Main Threads ends!!!");
    }

    private static boolean isFinish(Thread[] threads, Thread.State[] threadsStates) {
        printInfo(threads, threadsStates);
        for (int i = 0; i < threads.length; i++) {
            if (threads[i].getState() != Thread.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }

    private static void printInfo(Thread[] threads, Thread.State[] threadsStates) {
        for (int i = 0; i < threads.length; i++) {
            if (threads[i].getState() != threadsStates[i]) {
                System.out.println(format("Thread: Id: %d - Name: %s", threads[i].getId(), threads[i].getName()));
                System.out.println(format("Thread: Priority: %d", threads[i].getPriority()));
                System.out.println(format("Thread: Old State: %s - New State: %s", threadsStates[i], threads[i].getState()));
                }
        }
    }

    private static void getState(Thread.State[] threadsStates, Thread[] threads) {
        for (int i = 0; i < threads.length; i++) {
            threadsStates[i] = threads[i].getState();
        }
    }
}
@RequiredArgsConstructor
class PrimeNumberRunnable implements Runnable {
    @Override
    public void run() {
        int n = 20000;
        for (int i = 1; i < n; i++) {
            if (isPrime(i)) {
                System.out.println(format("T(%s): %d", Thread.currentThread().getName(), i));
            }
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