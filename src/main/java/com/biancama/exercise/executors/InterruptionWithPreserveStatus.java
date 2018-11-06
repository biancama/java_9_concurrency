package com.biancama.exercise.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 02/11/2018.
 */
public class InterruptionWithPreserveStatus {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executors = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2 ; i++) {
            executors.submit(printer());
        }

        TimeUnit.SECONDS.sleep(3);
        executors.shutdownNow();
        executors.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("Main Terminated");

    }



    static Runnable printer() {

        return () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    //Thread.currentThread().interrupt(); // preserve interruption status
                    System.out.println("Terminated !!!");
                    break;
                }
            }
        };
    }
}
