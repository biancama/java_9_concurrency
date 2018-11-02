package com.biancama.chap01;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 03/08/2017.
 */
@RequiredArgsConstructor
class FileSearchInterruptException implements Runnable {
    private final String initPath;
    private final String fileName;

    public void run() {
        File file = new File(initPath);
        if (file.isDirectory()) {
            try {
                directoryProcess(file);
            } catch (InterruptedException e) {
                System.out.printf("%s: The search has been interrupted",
                    Thread.currentThread().getName());
            }
        }
    }

    private void directoryProcess(File file) throws InterruptedException {
        File[] files = file.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                directoryProcess(files[i]);
            } else {
                fileProcess(files[i]);
            }
        }
    }

    private void fileProcess(File file) throws InterruptedException {
        if (file.getName().equals(fileName)) {
            System.out.printf("%s : %s\n",
                Thread.currentThread().getName(),
                file.getAbsolutePath());
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}
public class ThreadInterruptExceptionRecipe {
    public static void main(String[] args) {
        FileSearchInterruptException fileSearchInterruptException = new FileSearchInterruptException("/", "FileSearchInterruptException.java");

        Thread fileSearchTask = new Thread(fileSearchInterruptException);
        fileSearchTask.start();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileSearchTask.interrupt();
    }
}