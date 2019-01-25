package com.biancama.exercise.java9concurrency.c01;


import static java.lang.String.format;

/**
 * Created by massimo.biancalani on 25/01/2019.
 */
public class E007ExceptionHandler {

    public static void main(String[] args) {
        Thread t = new Thread(new ThreadThrowsAnException());
        t.setUncaughtExceptionHandler(new ThreadExceptionHandler());
        t.start();
    }
}

class ThreadThrowsAnException implements Runnable {

    @Override
    public void run() {
        Integer integerParsed = Integer.parseInt("ttt");
    }
}

class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(format("The thread %s, threw and exception %s", t.getName(), e));
    }
}
