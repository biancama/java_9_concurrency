package com.biancama.chap3;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class CompletableFutureRecipe {
    public static void main(String[] args) {
        System.out.printf("Main: Start\n");
        CompletableFuture<Integer> seedFuture = new CompletableFuture<>();
        Thread seedThread = new Thread(new SeedGenerator(seedFuture));
        seedThread.start();

        System.out.printf("Main: Getting the seed\n");
        int seed = 0;
        try {
            seed = seedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: The seed is: %d\n", seed);


        System.out.printf("Main: Launching the list of numbers generator\n");
        NumberListGenerator task = new NumberListGenerator(seed);
        CompletableFuture<List<Long>> startFuture = CompletableFuture
            .supplyAsync(task);


        System.out.printf("Main: Launching step 1\n");
        CompletableFuture<Long> step1Future = startFuture
            .thenApplyAsync(list -> {
                System.out.printf("%s: Step 1: Start\n",
                    Thread.currentThread().getName());
                long selected = 0;
                long selectedDistance = Long.MAX_VALUE;
                long distance;
                for (Long number : list) {
                    distance = Math.abs(number - 1000);
                    if (distance < selectedDistance) {
                        selected = number;
                        selectedDistance = distance;
                    }
                }
                System.out.printf("%s: Step 1: Result - %d\n",
                    Thread.currentThread().getName(), selected);
                return selected;
            });


        System.out.printf("Main: Launching step 2\n");
        CompletableFuture<Long> step2Future = startFuture
            .thenApplyAsync(list -> list.stream().max(Long::compare).get());

        CompletableFuture<Void> write2Future = step2Future
            .thenAccept(selected -> {
                System.out.printf("%s: Step 2: Result - %d\n",
                    Thread.currentThread().getName(), selected);
            });

        System.out.printf("Main: Launching step 3\n");
        NumberSelector numberSelector = new NumberSelector();
        CompletableFuture<Long> step3Future = startFuture
            .thenApplyAsync(numberSelector);


        System.out.printf("Main: Waiting for the end of the three steps\n");
            CompletableFuture<Void> waitFuture = CompletableFuture
                .allOf(step1Future, write2Future,
                    step3Future);



        CompletableFuture<Void> finalFuture = waitFuture
            .thenAcceptAsync((param) -> {
                System.out.printf("Main: The CompletableFuture example has been completed.");
            });
        finalFuture.join();

    }
}
@RequiredArgsConstructor
class SeedGenerator implements Runnable {
    private final CompletableFuture<Integer> resultCommunicator;
    @Override
    public void run() {
        System.out.printf("SeedGenerator: Generating seed...\n");
        // simulate long run operation
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int seed=nextInt(1, 10);
        resultCommunicator.complete(seed);

    }
}
@RequiredArgsConstructor
class NumberListGenerator implements Supplier<List<Long>> {
    private final int size;
    @Override
    public List<Long> get() {
        List<Long> ret = new ArrayList<>();
        for (int i=0; i< size*1000000; i++) {
            long number= nextLong();
            ret.add(number);
        }
        System.out.printf("%s : NumberListGenerator : End\n",
            Thread.currentThread().getName());
        return ret;
    }
}

class NumberSelector implements Function<List<Long>, Long> {
    @Override
    public Long apply(List<Long> list) {
        System.out.printf("%s: Step 3: Start\n",
            Thread.currentThread().getName());
        long max=list.stream().max(Long::compare).get();
        long min=list.stream().min(Long::compare).get();
        long result=(max+min)/2;
        System.out.printf("%s: Step 3: Result - %d\n",
            Thread.currentThread().getName(), result);
        return result;
    }
}
