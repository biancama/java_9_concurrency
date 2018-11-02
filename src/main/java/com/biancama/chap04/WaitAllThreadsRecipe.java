package com.biancama.chap04;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 07/08/2017.
 */
public class WaitAllThreadsRecipe {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<TaskResult> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TaskResult taskResult = new TaskResult("Task-" + i);
            tasks.add(taskResult);
        }
        List<Future<Result>> futures = executor.invokeAll(tasks);
        System.out.println("Main: Printing the results");
        for (int i = 0; i < futures.size(); i++) {
            Future<Result> future = futures.get(i);

            Result result = future.get();
            System.out.println(result.getName()+": "+result.getValue());
        }
        System.out.println("Main: Ends");
        executor.shutdown();
    }
}
@Getter
@Setter
class Result {
    private String name;
    private int value;

}
@RequiredArgsConstructor
class TaskResult implements Callable<Result> {
    private final String name;
    @Override
    public Result call() throws Exception {
        System.out.printf("%s: Staring\n",this.name);
        try {
            long duration=nextLong(1, 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name,duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int value=0;
        for (int i=0; i<5; i++){
            value+=nextInt(1, 100);
        }
        Result result=new Result();
        result.setName(this.name);
        result.setValue(value);
        System.out.println(this.name+": Ends");
        return result;
    }
}