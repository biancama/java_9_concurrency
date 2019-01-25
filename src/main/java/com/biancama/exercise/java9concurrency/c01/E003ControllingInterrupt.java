package com.biancama.exercise.java9concurrency.c01;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 24/01/2019.
 */
public class E003ControllingInterrupt {
    static String FILE_NAME = "fileSearched";

    /**
     * Search for a file for 10 seconds (at most) in a folder. Using a recursive search for nested folder
     *
     */

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new Searcher(FILE_NAME));

        t.start();
        for (int i= 0; i < 20; i++) {
            if (t.getState() == Thread.State.TERMINATED) {
                return;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }



        t.interrupt();


    }
}
@RequiredArgsConstructor
class Searcher implements Runnable {
    private final String fileName;
    @Override
    public void run() {
        MockFileSystem root = new MockFileSystem();

        while (true) {
            try {
                searchForFileName(root);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void searchForFileName(MockFileSystem root) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (root.isADirectory()) {
            MockFileSystem[] children = root.getChildren();
            for (int i = 0; i < children.length; i++) {
                searchForFileName(children[i]);
            }
        } else {
            if (root.getName().equals(fileName)) {
                System.out.println("File found!!!!!");
                throw new InterruptedException();
            }
        }
    }
}

class MockFileSystem {

    public boolean isADirectory() {
        return nextInt(0, 30) < 28;
    }

    public MockFileSystem[] getChildren() {
        int numberOfChildren = nextInt(0, 5);
        MockFileSystem[] result = new  MockFileSystem[numberOfChildren];
        for (int i = 0; i < numberOfChildren; i++) {
            result[i] = new MockFileSystem();
        }
        return result;
    }
    public String getName() {
        return nextInt(0, 30) < 28 ? randomAlphanumeric(5, 10) :E003ControllingInterrupt.FILE_NAME;
    }
}