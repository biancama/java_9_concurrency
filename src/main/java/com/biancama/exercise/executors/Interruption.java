package com.biancama.exercise.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 02/11/2018.
 *     Make a task that prints 0 through 9 on console.
 *     After printing a number the task should wait 1 sec before printing the next number.
 *     The task runs on a separate thread, other than main application thread.
 *     After starting the task the main application should wait for 3 sec and then shutdown.
 *     On shutdown the application should request the running task to finish.
 *     Before shutting down completely the application should, at the max, wait for 1 sec for the task to finish.
 *     The task should respond to the finish request by stopping immediately.
 */
public class Interruption {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        Future<?> threadSubmitted = executors.submit(printer());
        TimeUnit.SECONDS.sleep(3);
        threadSubmitted.cancel(true);
        executors.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("Main Terminated");
        executors.shutdownNow();
    }



    static Runnable printer() {

        return () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Terminated !!!");
                    break;
                }
            }
        };
    }
}
