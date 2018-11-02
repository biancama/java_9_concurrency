package com.biancama.chap01;

/**
 * Created by massimo.biancalani on 02/08/2017.
 */
public class PrimeGeneratorInterrupted extends Thread {
    @Override
    public void run() {
        long number=1L;
        while(true) {
            if (isPrime(number)) {
                System.out.printf("Number %d is Prime\n",number);
            }
            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted");
                return;
            }
            number ++;
        }
    }


    private boolean isPrime(long b) {
        if (b <= 2) return true;
        else if (b % 2 == 0) return false;
        else {
            for (int i = 2; i < b ; i++) {
                if (b % i == 0) return false;
            }
            return true;
        }
    }
}
class MainPrimeGeneratorInterrupted {
    public static void main(String[] args) throws InterruptedException {
        Thread task = new PrimeGeneratorInterrupted();
        task.start();
        Thread.sleep(2000);
        task.interrupt();
        System.out.printf("Main: Status of the Thread: %s\n",
            task.getState());
        System.out.printf("Main: isInterrupted: %s\n",
            task.isInterrupted());
        System.out.printf("Main: isAlive: %s\n", task.isAlive());
    }


}