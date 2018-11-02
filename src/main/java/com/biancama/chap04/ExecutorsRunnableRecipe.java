package com.biancama.chap04;

import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 07/08/2017.
 */
public class ExecutorsRunnableRecipe {
    public static void main(String[] args) {
        Server server=new Server();

        System.out.printf("Main: Starting.\n");
        for (int i=0; i<100; i++){
            Task task=new Task("Task "+i);
            server.executeTask(task);
        }


        System.out.printf("Main: Shuting down the Executor.\n");
        server.endServer();

        System.out.printf("Main: Sending another Task.\n");
        Task task=new Task("Rejected task");
        server.executeTask(task);

        System.out.printf("Main: End.\n");

    }
}

@RequiredArgsConstructor
class Task implements Runnable {
    private final Date initDate = new Date();
    private final String name;

    @Override
    public void run() {
        System.out.printf("%s: Task %s: Created on: %s\n",
            Thread.currentThread().getName(),
            name,initDate);
        System.out.printf("%s: Task %s: Started on: %s\n",
            Thread.currentThread().getName(),
            name,new Date());

        try {
            Long duration=nextLong(1, 10);
            System.out.printf("%s: Task %s: Doing a task during %d seconds\n", Thread.currentThread().getName(),
                name,duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("%s: Task %s: Finished on: %s\n",
            Thread.currentThread().getName(),
            name,new Date());

    }
}
class RejectedTaskController implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.printf("RejectedTaskController: The task %s has been rejected\n",r.toString());
        System.out.printf("RejectedTaskController: %s\n", executor.toString());
        System.out.printf("RejectedTaskController: Terminating: %s\n", executor.isTerminating());
        System.out.printf("RejectedTaksController: Terminated: %s\n", executor.isTerminated());
    }
}
class Server {
    private final ThreadPoolExecutor executor;
    public Server(){
        executor =( ThreadPoolExecutor ) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() );
        RejectedTaskController controller=new RejectedTaskController();
        executor.setRejectedExecutionHandler(controller);
    }

    public void executeTask(Task task) {
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task);
        System.out.printf("Server: Pool Size: %d\n",
            executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n",
            executor.getActiveCount());
        System.out.printf("Server: Task Count: %d\n",
            executor.getTaskCount());
        System.out.printf("Server: Completed Tasks: %d\n",
            executor.getCompletedTaskCount());

    }
    public void endServer() {
        executor.shutdown();
    }

}
