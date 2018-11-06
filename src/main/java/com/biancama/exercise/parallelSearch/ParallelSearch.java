package com.biancama.exercise.parallelSearch;

import static java.lang.String.format;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by massimo.biancalani on 06/11/2018.
 */
public class ParallelSearch {

    /**
     * This method creates as many threads as specified by numThreads, divides the
     * array A into that many parts, and gives each thread a part of the array to
     * search for x sequentially. If any thread finds x, then it returns an index i such
     * that A [ i ] = x. Otherwise, the method returns -1.
     *
     * @param x
     * @param A
     * @param numOfThreads
     * @return
     */
    public static int parallelSearch(int x, int[] A, int numOfThreads) throws InterruptedException, ExecutionException {
        int arrayLength = A.length;
        ExecutorService executors = Executors.newCachedThreadPool();
        List<Callable<Integer>> collables = new ArrayList<>();
        int sizeOfArrayPart = arrayLength/numOfThreads;

        for (int i = 0; i < numOfThreads; i++) {
            ParallelSearchCallable parallelSearchCallable = new ParallelSearchCallable(x, A, i*sizeOfArrayPart, (i +1)*sizeOfArrayPart);
            collables.add(parallelSearchCallable);
        }
        Integer result = executors.invokeAny(collables);
        executors.shutdownNow();
        if (result != null) {
            return result;
        } else {
            return -1;
        }

    }


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int N = 1000;

        int [] array= new int[N];

        for (int i = 0; i < N; i++) {
            array[i] = i + 1;
        }

        int x = nextInt(1, N + 100);
        int indexFound = parallelSearch(x, array, 10);
        System.out.println(format("%d found at position %d", x, indexFound));

    }

}

@AllArgsConstructor
class ParallelSearchCallable implements Callable<Integer> {
    private int x;
    private int[] A;
    private int startIndex;
    private int endIndex;

    @Override
    public Integer call() throws Exception {
        return parallelSearch(x, A, startIndex, endIndex);
    }

    private int parallelSearch(int x, int[] A, int startIndex, int endIndex) throws Exception {
        for (int i = startIndex; i < endIndex ; i++) {
            if (A[i] == x) {
                return i;
            }
        }
        throw new Exception("search not found");
    }
}