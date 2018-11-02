package com.biancama.chap3;

import static org.apache.commons.lang3.RandomUtils.nextInt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class CyclicBarrierRecipe {

    public static void main(String[] args) {
        final int ROWS=10000;
        final int NUMBERS=1000;
        final int SEARCH=5;
        final int PARTICIPANTS=5;
        final int LINES_PARTICIPANT=2000;
        MatrixMock mock=new MatrixMock(ROWS, NUMBERS,SEARCH);
        Results results=new Results(ROWS);
        Grouper grouper=new Grouper(results);
        CyclicBarrier barrier=new CyclicBarrier(PARTICIPANTS,grouper);
        Searcher searchers[]=new Searcher[PARTICIPANTS];
        for (int i=0; i<PARTICIPANTS; i++){
            searchers[i]=new Searcher(i*LINES_PARTICIPANT,
                (i*LINES_PARTICIPANT)+LINES_PARTICIPANT,
                mock, results, 5,barrier);
            Thread thread=new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("Main: The main thread has finished.\n");

    }
}
class MatrixMock {
    private final int data[][];
    public MatrixMock(int size, int length, int number) {
        int counter=0;
        data=new int[size][length];
        for (int i=0; i<size; i++) {
            for (int j=0; j<length; j++){
                data[i][j]= nextInt(1, 10);
                if (data[i][j]==number){
                    counter++;
                }
            }
        }
        System.out.printf("Mock: There are %d ocurrences of number in generated data.\n",counter,number);
    }

    public int[] getRow(int row){
        if ((row>=0)&&(row<data.length)){
            return data[row];
        }
        return null;
    }
}

class Results {
    @Getter
    private final int data[];
    public Results(int size){
        data=new int[size];
    }
    public void  setData(int position, int value){
        data[position]=value;
    }
}
@RequiredArgsConstructor
class Searcher implements Runnable {
    private final int firstRow;
    private final int lastRow;
    private final MatrixMock mock;
    private final Results results;
    private final int number;

    private final CyclicBarrier barrier;

    @Override
    public void run() {
        int counter;
        System.out.printf("%s: Processing lines from %d to %d.\n",
            Thread.currentThread().getName(),
            firstRow,lastRow);
        for (int i=firstRow; i<lastRow; i++){
            int row[]=mock.getRow(i);
            counter=0;
            for (int j=0; j<row.length; j++){
                if (row[j]==number){
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s: Lines processed.\n",
            Thread.currentThread().getName());
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }
}
@RequiredArgsConstructor
class Grouper implements Runnable {
    private final Results results;

    @Override
    public void run() {
        int finalResult=0;
        System.out.printf("Grouper: Processing results...\n");
        int data[]=results.getData();
        for (int number:data){
            finalResult+=number;
        }
        System.out.printf("Grouper: Total result: %d.\n", finalResult);

    }
}