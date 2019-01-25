package com.biancama.exercise.java9concurrency.c01;

import static java.lang.Runtime.getRuntime;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 24/01/2019.
 */
public class E006DaemonThread {

    /**
     * When a thread is marked as daemon thread, JVM doesn’t wait it to finish to terminate the program.
     * As soon as all the user threads are finished, JVM terminates the program as well as all the associated daemon threads.
     * @param args
     */

    public static void main(String[] args) {
        Deque<Event> eventDeque = new ConcurrentLinkedDeque<>();
        WriterTask writerTask = new WriterTask(eventDeque);

        for (int i = 0; i < getRuntime().availableProcessors(); i++) {
            Thread t =  new Thread(writerTask);
            //Thread t =  new Thread(new WriterTask(eventDeque));  this is the same

            t.start();
        }

        Thread t = new Thread(new CleanerTask(eventDeque));
        t.setDaemon(true);  // if I remove the cleaner won't stop
        t.start();


    }
}

@AllArgsConstructor
@Getter
class Event {
    private Date date;

    private String name;
}

@RequiredArgsConstructor
class WriterTask implements Runnable {

    private final Deque<Event> queue;

    @Override
    public void run() {
        for (int i = 0; i < 30; i++) {
            queue.addFirst(new Event(new Date(), randomAlphanumeric(5, 10)));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class CleanerTask implements Runnable {

    private final Deque<Event> queue;

    public CleanerTask(Deque<Event> queue) {
        this.queue = queue;

    }

    @Override
    public void run() {
        while (true) {
            clean(new Date());
        }
    }

    private void clean(Date date) {
        if (queue.size() == 0) {
            return;
        }
        boolean delete = false;
        long difference = 0;
        do {
            Event e = queue.getLast();
            difference = date.getTime() - e.getDate().getTime();
            if (difference > 10000) {
                System.out.println("Cleaner: Cleaned task " + e.getName());
                queue.removeLast();
                delete = true;
            }
        } while (difference > 10000);

        if (delete) {
            System.out.println("Cleaner: size of the queue " + queue.size());
        }
    }
}