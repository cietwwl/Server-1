package com.rw.fsutil.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 
 * @author Jamaz
 */
public class FixTaskLatch {

    private final AtomicReference<BooleanLatch> latch;
    private final Runnable task;

    public FixTaskLatch(Runnable task) {
        this.task = task;
        this.latch = new AtomicReference<BooleanLatch>();
    }
    /**

     * 重置任务为未被执行的状态
     */
    public void reset() {
        latch.set(null);
    }

    /**
     * 请求执行任务，如果任务未被执行，会获得任务的执行权并执行。
     * 如果任务正在执行，会阻塞到任务执行完
     * 如果任务已经执行，会退出方法
     */
    public void acquire() {
        for (;;) {
            BooleanLatch old = latch.get();
            if (old != null) {
                old.acquire();
                break;
            }
            BooleanLatch current = new BooleanLatch();
            if (!this.latch.compareAndSet(null, current)) {
                continue;
            }
            try {
                task.run();
                break;
            } finally {
                current.release();
            }
        }
    }

    public static void main(String[] args) {
        final FixTaskLatch t = new FixTaskLatch(new Runnable() {

            public Object o = null;

            @Override
            public void run() {
                System.out.println("开始做任务.." + Thread.currentThread());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
//                if (true) {
//                    throw new RuntimeException();
//                }
                int i = 3 / 0;
                o = new Object();

                System.out.println("任务做完啦" + Thread.currentThread());
            }

            @Override
            public String toString() {
                return "object = " + o;
            }
        });
        for (int i = 0; i < 5; i++) {
            new Thread() {

                public void run() {
                    System.out.println("请求通过:" + Thread.currentThread());
                    t.acquire();
                    System.out.println("通过:" + Thread.currentThread() + "o = " + t.task);
                }
            }.start();
        }
    }

    public interface Task {

        public void execute();

        public Object get();
    }
    
}
