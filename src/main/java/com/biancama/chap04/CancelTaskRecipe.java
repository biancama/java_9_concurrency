package com.biancama.chap04;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 07/08/2017.
 */
public class CancelTaskRecipe {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor= (ThreadPoolExecutor) Executors.newCachedThreadPool();
        TaskCancelling taskCancelling = new TaskCancelling();
        System.out.printf("Main: Executing the Task\n");
        Future<String> future = executor.submit(taskCancelling);

        TimeUnit.SECONDS.sleep(2);

        System.out.printf("Main: Canceling the Task\n");
        future.cancel(true);
        System.out.printf("Main: Canceled: %s\n",future.isCancelled());
        System.out.printf("Main: Done: %s\n",future.isDone());


        TaskCancelling1 taskCancelling1 = new TaskCancelling1();
        System.out.printf("Main: Executing the Task\n");
        Future<String> future1 = executor.submit(taskCancelling);

        TimeUnit.SECONDS.sleep(2);

        System.out.printf("Main: Canceling the Task\n");
        future1.cancel(true);
        System.out.printf("Main: Canceled: %s\n",future1.isCancelled());
        System.out.printf("Main: Done: %s\n",future1.isDone());

        executor.shutdown();

    }
}
class TaskCancelling implements Callable<String> {

    @Override
    public String call() throws Exception {
        while (true) {
            System.out.printf("Task: Test\n");
            Thread.sleep(100);
        }
    }
}

class TaskCancelling1 implements Callable<String> {

    @Override
    public String call() throws Exception {
        while (Thread.currentThread().isInterrupted()) {
            System.out.printf("Task: Test\n");
        }
        return randomAlphanumeric(1, 100);
    }
}
