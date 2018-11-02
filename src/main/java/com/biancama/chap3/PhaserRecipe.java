package com.biancama.chap3;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class PhaserRecipe {
    public static void main(String[] args) {
        // Creates a Phaser with three participants
        Phaser phaser=new Phaser(3);

        // Creates 3 FileSearch objects. Each of them search in different directory
        FileSearch system=new FileSearch("/home", "log", phaser);
        FileSearch apps=new FileSearch("/tmp","log",phaser);
        FileSearch documents=new FileSearch("/Downlods","log",phaser);

        // Creates a thread to run the system FileSearch and starts it
        Thread systemThread=new Thread(system,"System");
        systemThread.start();

        // Creates a thread to run the apps FileSearch and starts it
        Thread appsThread=new Thread(apps,"Apps");
        appsThread.start();

        // Creates a thread to run the documents  FileSearch and starts it
        Thread documentsThread=new Thread(documents,"Documents");
        documentsThread.start();
        try {
            systemThread.join();
            appsThread.join();
            documentsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Terminated: %s\n",phaser.isTerminated());
    }
}
@RequiredArgsConstructor
class FileSearch implements Runnable {

    private final String initPath;

    private final String fileExtension;


    private List<String> results = new ArrayList<>();

    private final Phaser phaser;

    @Override
    public void run() {
        phaser.arriveAndAwaitAdvance();

        System.out.printf("%s: Starting.\n", Thread.currentThread().getName());

        // 1st Phase: Look for the files
        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }
    // If no results, deregister in the phaser and ends
        if (!checkResults()) {
            return;
        }

        // 2nd Phase: Filter the results
        filterResults();

        // If no results after the filter, deregister in the phaser and ends
        if (!checkResults()) {
            return;
        }

        // 3rd Phase: Show info
        showInfo();
        phaser.arriveAndDeregister();
        System.out.printf("%s: Work completed.\n", Thread.currentThread().getName());
    }

    /**
     * This method prints the final results of the search
     */
    private void showInfo() {
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        // Waits for the end of all the FileSearch threads that are registered
        // in the phaser
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * This method checks if there are results after the execution of a phase.
     * If there aren't results, deregister the thread of the phaser.
     *
     * @return true if there are results, false if not
     */
    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.printf("%s: Phase %d: 0 results.\n", Thread.currentThread().getName(), phaser.getPhase());
            System.out.printf("%s: Phase %d: End.\n", Thread.currentThread().getName(), phaser.getPhase());
            // No results. Phase is completed but no more work to do. Deregister
            // for the phaser
            phaser.arriveAndDeregister();
            return false;
        } else {
            // There are results. Phase is completed. Wait to continue with the
            // next phase
            System.out.printf("%s: Phase %d: %d results.\n", Thread.currentThread().getName(), phaser.getPhase(),
                results.size());
            phaser.arriveAndAwaitAdvance();
            return true;
        }
    }

    /**
     * Method that filter the results to delete the files modified more than a
     * day before now
     */
    private void filterResults() {
        List<String> newResults = new ArrayList<>();
        long actualDate = new Date().getTime();
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            long fileDate = file.lastModified();

            if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                newResults.add(results.get(i));
            }
        }
        results = newResults;
    }

    /**
     * Method that process a directory
     *
     * @param file
     *            : Directory to process
     */
    private void directoryProcess(File file) {

        // Get the content of the directory
        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // If is a directory, process it
                    directoryProcess(list[i]);
                } else {
                    // If is a file, process it
                    fileProcess(list[i]);
                }
            }
        }
    }

    /**
     * Method that process a File
     *
     * @param file
     *            : File to process
     */
    private void fileProcess(File file) {
        if (file.getName().endsWith(fileExtension)) {
            results.add(file.getAbsolutePath());
        }
    }
}