package com.biancama.exercise.threads;


import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 02/11/2018.
 * The following are the requirements of the use case:
 *
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
        Thread t = new Thread(printer());
        t.start();                                      // requirement 3
        TimeUnit.SECONDS.sleep(3);              // requirement 4
        t.interrupt();                                   // requirement 5
        t.join(1_000);                             // requirement 6
        System.out.println("Main Terminated");
    }

    private static Runnable  printer() {  // simple thread for printing 0..9

        return () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Terminated !!!");
                    break;   // requirement 7
                }
            }
        };
    }
}

