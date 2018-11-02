package com.biancama.chap01;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 03/08/2017.
 */
class DataSourcesLoaderJoin implements Runnable  {
    public void run() {
        System.out.printf("Beginning data sources loading: %s\n",
            new Date());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Data sources loading has finished: %s\n",
            new Date());
    }
}
class NetworkConnectionsLoader implements Runnable {
    public void run() {
        System.out.printf("Beginning data sources loading: %s\n",
            new Date());
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Data sources loading has finished: %s\n",
            new Date());
    }
}
public class ThreadJoinRecipe {
    public static void main(String[] args) {
        DataSourcesLoaderJoin dsLoader = new DataSourcesLoaderJoin();
        Thread thread1 = new Thread(dsLoader,"DataSourceThread");
        NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
        Thread thread2 = new Thread(ncLoader,"NetworkConnectionLoader");
        thread1.start();
        thread2.start();


        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Configuration has been loaded: %s\n",
            new Date());

    }
}