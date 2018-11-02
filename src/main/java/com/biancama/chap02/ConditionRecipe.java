package com.biancama.chap02;

import static org.apache.commons.lang3.RandomUtils.nextInt;

import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class ConditionRecipe {

    public static void main(String[] args) {
        FileMock mock = new FileMock(100, 10);
        Buffer buffer = new Buffer(20);
        Thread producerThread = new Thread(new ProducerCondition(mock, buffer),"Producer");
        Thread consumersThreads[] = new Thread[3];
        for (int i=0; i<3; i++){
            consumersThreads[i] = new Thread(new ConsumerCondition(buffer),"Consumer "+i);
        }
        producerThread.start();
        for (int i = 0; i< 3; i++){
            consumersThreads[i].start();
        }

    }
}
class FileMock {

    private String[] content;
    private int index;
    public FileMock(int size, int length){
        content = new String[size];
        for (int i = 0; i< size; i++){
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++){
                int randomCharacter= nextInt();
                buffer.append((char)randomCharacter);
            }
            content[i] = buffer.toString();
        }
        index=0;
    }

    public boolean hasMoreLines(){
        return index <content.length;
    }

    public String getLine(){
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length-index));
            return content[index++];
        }
        return null;
    }

}

class Buffer {
    private final LinkedList<String> buffer;
    private final int maxSize;
    private final ReentrantLock lock;
    private final Condition lines;

    private final Condition space;
    private boolean pendingLines;

    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines =true;
    }
    public void insert(String line) throws InterruptedException {
        try {
            lock.lock();
            while (buffer.size() == maxSize) {
                space.await();
            }
            buffer.offer(line);
            lines.signalAll();
        } finally {
            lock.unlock();
        }
    }
    public String get() throws InterruptedException {
        String result = null;
        try {
            lock.lock();
            while(buffer.size() == 0) {
                lines.await();
            }
            result = buffer.poll();
            space.signalAll();
        } finally {
             lock.unlock();
        }
        return result;
    }
    public synchronized void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }
    public synchronized boolean hasPendingLines() {
        return pendingLines || buffer.size()>0;
    }

}
@RequiredArgsConstructor
class ProducerCondition implements Runnable {
    private final FileMock mock;

    private final Buffer buffer;

    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            try {
                buffer.insert(mock.getLine());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        buffer.setPendingLines(false);
    }
}
@RequiredArgsConstructor
class ConsumerCondition implements Runnable {
    private final Buffer buffer;
    @Override
    public void run() {
        while (buffer.hasPendingLines()) {
            String line = null;
            try {
                line = buffer.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processLine(line);
        }
    }

    private void processLine(String line) {
        try {

            Thread.sleep(nextInt(1, 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
