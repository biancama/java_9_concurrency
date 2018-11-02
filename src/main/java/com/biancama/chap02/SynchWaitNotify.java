package com.biancama.chap02;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by massimo.biancalani on 03/08/2017.
 */
public class SynchWaitNotify {
    public static void main(String[] args) {
        EventStorage eventStorage = new EventStorage();
        Thread t1 = new Thread(new Producer(eventStorage));
        Thread t2 = new Thread(new Consumer(eventStorage));
        t1.start();
        t2.start();
    }
}

class EventStorage {
    private final static int MAX_SIZE = 10;
    private Queue<Date> storage = new LinkedList<>();

    public synchronized void set() throws InterruptedException {
        while (storage.size() == MAX_SIZE) {
            wait();
        }
        storage.offer(new Date());
        System.out.printf("Set: %d\n",storage.size());
        notify();
    }

    public synchronized void get() throws InterruptedException {
        while (storage.size() == 0) {
            wait();
        }
        String result = storage.poll().toString();
        System.out.printf("get: %s\n", result);
        notify();
    }
}
@RequiredArgsConstructor
class Producer implements Runnable {
    private final EventStorage eventStorage;
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                eventStorage.set();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

@RequiredArgsConstructor
class Consumer implements Runnable {
    private final EventStorage eventStorage;

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                eventStorage.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}