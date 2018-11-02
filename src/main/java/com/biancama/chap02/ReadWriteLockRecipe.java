package com.biancama.chap02;

import static org.apache.commons.lang3.RandomUtils.nextDouble;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class ReadWriteLockRecipe {
    public static void main(String[] args) {
        PricesInfo pricesInfo = new PricesInfo();
        Thread threadsReader[]=new Thread[5];
        for (int i = 0; i < 5; i++) {
            threadsReader[i] = new Thread(new Reader(pricesInfo));
        }
        Thread threadWriter = new Thread(new Writer(pricesInfo));
        for (int i = 0; i < 5; i++) {
            threadsReader[i].start();
        }
        threadWriter.start();
    }
}
class PricesInfo {
    private double price1=1.0;
    private double price2=2.0;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public double getPrice1() {
        lock.readLock().lock();
        double value=price1;
        lock.readLock().unlock();
        return value;
    }
    public double getPrice2() {
        lock.readLock().lock();
        double value=price2;
        lock.readLock().unlock();
        return value;
    }
    public void setPrices(double price1, double price2) {
        lock.writeLock().lock();
        System.out.printf("%s: PricesInfo: Write Lock Adquired.\n",
            new Date());
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.price1=price1;
        this.price2=price2;
        System.out.printf("%s: PricesInfo: Write Lock Released.\n",
            new Date());
        lock.writeLock().unlock();
    }

}
@RequiredArgsConstructor
class Reader implements Runnable {
    private final PricesInfo pricesInfo;
    @Override
    public void run() {
        for (int i=0; i<20; i++){
            System.out.printf("%s: %s: Price 1: %f\n",new Date(),
                Thread.currentThread().getName(),
                pricesInfo.getPrice1());
            System.out.printf("%s: %s: Price 2: %f\n",new Date(),
                Thread.currentThread().getName(),
                pricesInfo.getPrice2());
        }

    }
}
@RequiredArgsConstructor
class Writer implements Runnable {
    private final PricesInfo pricesInfo;

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.printf("%s: Writer: Attempt to modify the prices.\n", new Date());
            pricesInfo.setPrices(nextDouble(), nextDouble());
            System.out.printf("%s: Writer: Prices have been modified.\n", new Date());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}