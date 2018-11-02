package com.biancama.chap02;

import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class ReentrantLockRecipe {
    public static void main(String[] args) throws InterruptedException {
        System.out.printf("Running example with fair-mode = false\n");
        testPrintQueue(false);
        System.out.printf("Running example with fair-mode = true\n");
        testPrintQueue(true);
    }

    private static void testPrintQueue(boolean b) throws InterruptedException {
        PrintQueue printQueue = new PrintQueue(b);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            Job job = new Job(printQueue);
            Thread task = new Thread(job);
            threads[i] = task;
            task.start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
    }
}
class PrintQueue {


    private final Lock queueLock;

    PrintQueue(boolean fairMode) {
        this.queueLock = new ReentrantLock(fairMode);
    }

    public void printJob(Object document) throws InterruptedException {
        queueLock.lock();
        try {
            Long duration= nextLong(1000, 5000);
            System.out.println(Thread.currentThread().getName()+ ": PrintQueue: Printing a Job during "+
            (duration/1000)+" seconds");
            Thread.sleep(duration);
        } finally {
            queueLock.unlock();
        }
    }
}
@RequiredArgsConstructor
class Job implements Runnable {
    private final PrintQueue printQueue;
    @Override
    public void run() {
        System.out.printf("%s: Going to print a document\n",
            Thread.currentThread().getName());
        try {
            printQueue.printJob(new Object());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: The document has been printed\n",
            Thread.currentThread().getName());
    }
}

