package com.biancama.chap04;

import static org.apache.commons.lang3.RandomUtils.nextInt;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 07/08/2017.
 */
public class ExecutorsCallableRecipe {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<Future<Integer>> futureList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Integer number = nextInt(1, 10);
            FactorialCalculator factorialCalculator = new FactorialCalculator(number);
            futureList.add(executor.submit(factorialCalculator));
        }

        do {
            System.out.printf("Main: Number of Completed Tasks: %d\n",
                executor.getCompletedTaskCount());
            for (int i = 0; i < futureList.size(); i++) {
                Future<Integer> result = futureList.get(i);
                System.out.printf("Main: Task %d: %s\n", i, result.isDone());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } while (executor.getCompletedTaskCount() < futureList.size());

        System.out.printf("Main: Results\n");
        for (int i = 0; i < futureList.size(); i++) {
            Future<Integer> result = futureList.get(i);
            Integer number = null;
            try {
                number = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Main: Task %d: %d\n",i,number);
        }
        executor.shutdown();


    }
}
@RequiredArgsConstructor
class FactorialCalculator implements Callable<Integer> {
    private final Integer number;

    @Override
    public Integer call() throws Exception {
        int result = 1;
        for (int i = 1; i <= number; i++) {
            result *= i;
            TimeUnit.MILLISECONDS.sleep(20);
        }
        System.out.printf("%s: %d\n",Thread.currentThread().getName(),
            result);

        return result;
    }
}