package com.biancama.chap01;

import java.io.PrintStream; /**
 * Created by massimo.biancalani on 02/08/2017.
 */
class Calculator implements Runnable {
    public void run() {
        long current = 1L;
        long max = 20000L;
        long numPrimes = 0;
        System.out.printf("Thread '%s': START\n",
            Thread.currentThread().getName());
        while (current <= max) {
            if (isPrime(current)) {
                numPrimes++;
            }
            current++;
        }
        System.out.printf("Thread '%s': END. Number of Primes: %d\n",
            Thread.currentThread().getName(), numPrimes);
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

public class SimpleThreadRecipe {
    public static void main(String[] args) {
        // Thread priority infomation
        System.out.printf("Minimum Priority: %s\n", Thread.MIN_PRIORITY);
        System.out.printf("Normal Priority: %s\n", Thread.NORM_PRIORITY);
        System.out.printf("Maximun Priority: %s\n", Thread.MAX_PRIORITY);

        Thread threads[];
        Thread.State status[];
        // Launch 10 threads to do the operation, 5 with the max
        // priority, 5 with the min
        threads = new Thread[10];
        status = new Thread.State[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator());
            if ((i % 2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("My Thread " + i);
        }

        // Write the status of the threads
        for (int i = 0; i < 10; i++) {
            System.out.println("Main : Status of Thread " + i + " : " + threads[i].getState());
            status[i] = threads[i].getState();
        }
        // Start the ten threads
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        // Wait for the finalization of the threads. We save the status of
        // the threads and only write the status if it changes.
        boolean finish = false;
        while (!finish) {
            for (int i = 0; i < 10; i++) {
                if (threads[i].getState() != status[i]) {
                    writeThreadInfo(System.out, threads[i], status[i]);
                    status[i] = threads[i].getState();
                }
            }

            finish = true;
            for (int i = 0; i < 10; i++) {
                finish = finish && (threads[i].getState() == Thread.State.TERMINATED);
            }
        }

    }

    private static void writeThreadInfo(PrintStream out, Thread thread, Thread.State status) {
        out.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
        out.printf("Main : Priority: %d\n", thread.getPriority());
        out.printf("Main : Old State: %s\n", status);
        out.printf("Main : New State: %s\n", thread.getState());
        out.printf("Main : ************************************\n");
    }
}
