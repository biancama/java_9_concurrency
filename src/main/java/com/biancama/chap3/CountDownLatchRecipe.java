package com.biancama.chap3;

import static org.apache.commons.lang3.RandomUtils.nextLong;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class CountDownLatchRecipe {
    public static void main(String[] args) {
        VideoConference conference=new VideoConference(10);
        Thread threadConference=new Thread(conference);
        threadConference.start();
        for (int i=0; i<10; i++){
            Participant p=new Participant(conference, "Participant "+i);
            Thread t=new Thread(p);
            t.start();
        }

    }
}
class VideoConference implements Runnable {
    private final CountDownLatch controller;

    VideoConference(int number) {
        this.controller = new CountDownLatch(number);
    }
    public void arrive(String name) {
        System.out.printf("%s has arrived.",name);
        controller.countDown();
        System.out.printf("VideoConference: Waiting for %d participants.\n",controller.getCount());


    }

        @Override
    public void run() {
        System.out.printf("VideoConference: Initialization: %d participants.\n",controller.getCount());
        try {
            controller.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            System.out.printf("VideoConference: All the participants have come\n");
            System.out.printf("VideoConference: Let's start...\n");
    }
}
@RequiredArgsConstructor
class Participant implements Runnable {
    private final VideoConference videoConference;
    private final String name;

    @Override
    public void run() {

        long duration=nextLong(1, 10);
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        videoConference.arrive(name);
    }
}