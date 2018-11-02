package com.biancama.chap3;

import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class SemaphoreRecipe {
    public static void main(String[] args) {
        PrintQueue printQueue=new PrintQueue();
        Thread[] threads=new Thread[12];
        for (int i=0; i < threads.length; i++){
            threads[i]=new Thread(new Job(printQueue),"Thread"+i);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
}
class PrintQueue {
    private final Semaphore semaphore;
    private final boolean freePrinters[];
    private final Lock lockPrinters;
    private int printer;

    public PrintQueue() {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for (int i = 0; i < 3; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    public void printJob(Object document) throws InterruptedException {
        try {
            semaphore.acquire();
            int assignedPrinter = getPrinter();
            long duration = nextLong(1, 10);
            System.out.printf("%s - %s: PrintQueue: Printing a Job in Printer %d during %d seconds\n",
                new Date(), Thread.currentThread().getName(),
                assignedPrinter, duration);
            TimeUnit.SECONDS.sleep(duration);
            // Free the printer
            freePrinters[assignedPrinter]=true;
        } finally {
            semaphore.release();
        }
    }

    public int getPrinter() {
        int ret = -1;
        try {
            lockPrinters.lock();
            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret = i;
                    freePrinters[i] = false;
                    break;
                }
            }
        } finally {
            lockPrinters.unlock();
        }
        return ret;
    }
}
@RequiredArgsConstructor
class Job implements Runnable {
    private final PrintQueue printQueue;

    @Override
    public void run() {
        System.out.printf("%s: Going to print a job\n",
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