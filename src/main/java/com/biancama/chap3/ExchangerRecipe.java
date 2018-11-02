package com.biancama.chap3;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class ExchangerRecipe {
    public static void main(String[] args) {
        List<String> buffer1=new ArrayList<>();
        List<String> buffer2=new ArrayList<>();
        Exchanger<List<String>> exchanger=new Exchanger<>();
        Producer producer=new Producer(buffer1, exchanger);
        Consumer consumer=new Consumer(buffer2, exchanger);
        Thread threadProducer=new Thread(producer);
        Thread threadConsumer=new Thread(consumer);

        threadProducer.start();
        threadConsumer.start();


    }
}
@AllArgsConstructor
@RequiredArgsConstructor
class Producer implements Runnable {
    private List<String> buffer;
    private final Exchanger<List<String>> exchanger;

    @Override
    public void run() {
        for (int cycle = 1; cycle <= 10; cycle++) {
            System.out.printf("Producer: Cycle %d\n", cycle);
            for (int j = 0; j < 10; j++) {
                String message="Event "+(((cycle-1)*10)+j);
                System.out.printf("Producer: %s\n",message);
                buffer.add(message);
            }
            try {
                buffer=exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
@AllArgsConstructor
@RequiredArgsConstructor
class Consumer implements Runnable {
    private List<String> buffer;
    private final Exchanger<List<String>> exchanger;

    @Override
    public void run() {
        for (int cycle = 1; cycle <= 10; cycle++){
            System.out.printf("Consumer: Cycle %d\n",cycle);

            try {
                // Wait for the produced data and send the empty buffer to the producer
                buffer=exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Consumer: %d\n",buffer.size());

            for (int j=0; j<10; j++){
                String message=buffer.get(0);
                System.out.printf("Consumer: %s\n",message);
                buffer.remove(0);
            }

        }

    }
}