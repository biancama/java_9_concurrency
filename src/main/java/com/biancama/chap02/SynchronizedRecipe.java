package com.biancama.chap02;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * Created by massimo.biancalani on 03/08/2017.
 */
class ParkingCash {
    private static final int cost=2;
    private long cash = 0;

    public synchronized void vehiclePay() {
        cash+=cost;
    }

    public void close() {
        System.out.printf("Closing accounting");
        long totalAmmount;
        synchronized (this) {
            totalAmmount=cash;
            cash=0;
        }
        System.out.printf("The total amount is : %d",
            totalAmmount);
    }
}
@RequiredArgsConstructor
@Getter
class ParkingStats {
    private long numberCars = 0;
    private long numberMotorcycles = 0;
    private final ParkingCash cash;
    private final Object controlCar = new Object();
    private final Object controlMotor = new Object();
    public void carComeIn() {
        synchronized (controlCar) {
            numberCars++;
        }
    }

    public void carGoOut() {
        synchronized (controlCar) {
            numberCars--;
        }
        cash.vehiclePay();
    }
    public void motoComeIn() {
        synchronized (controlMotor) {
            numberMotorcycles++;
        }
    }

    public void motoGoOut() {
        synchronized (controlMotor) {
            numberMotorcycles--;

        }
        cash.vehiclePay();
    }
}
@RequiredArgsConstructor
class Sensor implements Runnable {
    private final ParkingStats stats;
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            stats.carComeIn();
            stats.carComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stats.motoComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stats.motoGoOut();
            stats.carGoOut();
            stats.carGoOut();
        }
    }
}
public class SynchronizedRecipe {
    public static void main(String[] args) {
        ParkingCash cash = new ParkingCash();
        ParkingStats stats = new ParkingStats(cash);

        System.out.printf("Parking Simulator\n");

        int numberOFSensors = 2 * Runtime.getRuntime()
            .availableProcessors();
        Thread threads[]=new Thread[numberOFSensors];
        for (int i = 0; i < numberOFSensors; i++) {
            Sensor sensor=new Sensor(stats);
            Thread thread=new Thread(sensor);
            thread.start();
            threads[i] = thread;
        }

        // final sensors
        for (int i=0; i< numberOFSensors; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Number of cars: %d\n",
            stats.getNumberCars());
        System.out.printf("Number of motorcycles: %d\n",
            stats.getNumberMotorcycles());
        cash.close();
    }
}
