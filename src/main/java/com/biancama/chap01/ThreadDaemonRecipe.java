package com.biancama.chap01;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 03/08/2017.
 */
@RequiredArgsConstructor
class WriterTaskDaemon implements Runnable {
    private final Deque<Event> deque;
    public void run() {
        for (int i = 0; i < 50; i++) {
            Event event = new Event();
            event.setDate(new Date());
            event.setEvent(String.format("The thread %s has generated an event", Thread.currentThread().getId()));
            deque.addFirst(event);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class CleanerTask extends Thread {
    private final Deque<Event> deque;

    CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    private void clean(Date date) {
        long difference;
        boolean delete;

        delete = false;
        do {
            if (deque.size() == 0) {
                return;
            }
            Event e = deque.getLast();
            difference = date.getTime() - e.getDate().getTime();
            if (difference > 10000) {
                System.out.printf("Cleaner: %s\n",e.getEvent());
                deque.removeLast();
                delete=true;
            }
        } while (difference > 10000);

        if (delete) {
            System.out.printf("Cleaner: Size of the queue: %d\n",
                deque.size());
        }


    }
}
@Data
class Event {
    private Date date;
    private String event;
}
public class ThreadDaemonRecipe {
    public static void main(String[] args) {
        Deque<Event> deque=new ConcurrentLinkedDeque<Event>();
        WriterTaskDaemon writerTaskDaemon = new WriterTaskDaemon(deque);
        CleanerTask cleanerTask = new CleanerTask(deque);

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            Thread writerTask = new Thread(writerTaskDaemon);
            writerTask.start();
        }
        cleanerTask.start();
    }
}