package com.biancama.chap02;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by massimo.biancalani on 04/08/2017.
 */
public class StampedLockRecipe {
    public static void main(String[] args) {
        Position position=new Position();
        StampedLock lock=new StampedLock();

        Thread threadWriter=new Thread(new WriterStampedLock(position,lock));
        Thread threadReader=new Thread(new ReaderStampedLock(position, lock));
        Thread threadOptReader=new Thread(new OptimisticReaderStampedLock(position, lock));

        threadWriter.start();
        threadReader.start();
        threadOptReader.start();

        try {
            threadWriter.join();
            threadReader.join();
            threadOptReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@RequiredArgsConstructor
class WriterStampedLock implements Runnable {
    private final Position position;
    private final StampedLock lock;
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            long stamp = lock.writeLock();

            try {
                System.out.printf("Writer: Lock acquired %d\n", stamp);
                position.setX(position.getX() + 1);
                position.setY(position.getY() + 1);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlockWrite(stamp);
                System.out.printf("Writer: Lock released %d\n", stamp);
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
@RequiredArgsConstructor
class ReaderStampedLock implements Runnable {
    private final Position position;
    private final StampedLock lock;

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            long stamp = lock.readLock();
            try {
                System.out.printf("Reader: %d - (%d,%d)\n", stamp,
                    position.getX(), position.getY());
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlockRead(stamp);
                System.out.printf("Reader: %d - Lock released\n", stamp);
            }
        }
    }
}
@RequiredArgsConstructor
class OptimisticReaderStampedLock implements Runnable {
    private final Position position;
    private final StampedLock lock;

    @Override
    public void run() {
        long stamp;
        for (int i = 0; i < 100; i++) {
            try {
                stamp=lock.tryOptimisticRead();
                int x = position.getX();
                int y = position.getY();
                if (lock.validate(stamp)) {
                    System.out.printf("OptmisticReader: %d - (%d,%d)\n",
                        stamp,x, y);
                } else {
                    System.out.printf("OptmisticReader: %d - Not Free\n",
                        stamp);
                }
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


@Getter
@Setter
class Position {
    private int x;
    private int y;
}