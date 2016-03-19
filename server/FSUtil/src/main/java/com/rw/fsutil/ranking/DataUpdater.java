package com.rw.fsutil.ranking;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * 继承此类可以实现数据定时更新的功能。
 * 更新周期由构造函数的参数决定，单位是秒。当有数据发生变化时，可以
 * 通过外部调用{@link #submitUpdateTask() }触发更新周期的倒计时，
 * 以达到在同一周期内，多次数据发生变化，只会更新到数据库一次的效果。
 * 重写{@link #notifyDataUpdated() }实现数据更新的具体行为。
 * 当回调{@link #notifyDataUpdated() }返回false时，会在下一周期到来时再次回调
 * {@link #notifyDataUpdated() }，直到返回true
 * 调用刷新方法{@link #flush() },无论数据有没更新都会以异步的方式更新数据。
 * </pre>
 * @author jamaz
 */
public abstract class DataUpdater {

    private final ScheduledExecutorService executor;
    private final AtomicBoolean isDirty = new AtomicBoolean(false);
    private final long nano;

    public DataUpdater(long period,ScheduledExecutorService executor) {
//        if (period < 1) {
//            throw new ExceptionInInitializerError("period < 1");
//        }
        if(executor == null){
        	throw new ExceptionInInitializerError("scheduled executor is not initialized");
        }
        this.executor = executor;
        this.nano = TimeUnit.SECONDS.toNanos(period);
    }

    /**
     * 通知数据更新(进行数据同步DB的操作)，如果更新到数据失败，则返回false
     */
    public abstract boolean notifyDataUpdated();

    /**
     * 提交一个更新任务
     */
    public  void submitUpdateTask() {
        if (isDirty.compareAndSet(false, true)) {
            executor.schedule(periodCallable, nano, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * 刷新数据的更新，将会以异步的方式回调{@link #notifyDataUpdated() }
     */
    public final void flush() {
        executor.schedule(flushCallable, 0, TimeUnit.NANOSECONDS);
    }
    
    //刷新Callable
    private Callable flushCallable = new Callable() {

        @Override
        public Object call() throws Exception {
            boolean old = isDirty.getAndSet(false);
            boolean result = notifyDataUpdated();
            if (!result && old) {
                submitUpdateTask();
            }
            return null;
        }
    };
    
    //周期Callable
    private Callable periodCallable = new Callable() {

        @Override
        public Object call() throws Exception {
            try {
                if (isDirty.compareAndSet(true, false) && !notifyDataUpdated()) {
                    submitUpdateTask();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    };
}
