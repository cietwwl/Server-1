package com.rw.fsutil.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 基于布尔值的锁存器
 * 在完成一个操作前允许一个或多个线程进行等待
 * @author Jamaz
 */
public class BooleanLatch {

    private static class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected int tryAcquireShared(int ignore) {
            return getState() != 0 ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignore) {
            setState(1);
            return true;
        }
    }
    private final Sync sync = new Sync();

    /**
     * 释放锁存器，唤醒所有阻塞线程并允许后续线程通过
     */
    public void release() {
        sync.releaseShared(1);
    }

    /**
     * 请求通过，如果操作没完成，会一直等待
     */
    public void acquire() {
        sync.acquireShared(1);
    }

    public static void main(String[] args) throws InterruptedException {
        BooleanLatch b = new BooleanLatch();
        for (int i = 0; i < 10; i++) {
            new Latch(b, i).start();
        }
        Thread.sleep(2000);
        b.release();
    }

    static class Latch extends Thread {

        final BooleanLatch b;
        final int name;

        public Latch(BooleanLatch b, int name) {
            this.b = b;
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("请求:" + name);
            b.acquire();
            System.out.println("请求通过:" + name);
        }
    }
}
