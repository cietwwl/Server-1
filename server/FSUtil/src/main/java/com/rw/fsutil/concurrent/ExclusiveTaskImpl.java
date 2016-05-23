package com.rw.fsutil.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExclusiveTaskImpl<K> {

	private long timeMillis;
	private ConcurrentHashMap<K, ReentrantFutureTask<?>> taskMap;

	public <V> V execute(K key, Callable<V> callable) throws TimeoutException {
		ReentrantFutureTask<V> task = new ReentrantFutureTask<V>(key, callable);
		try {
			return execute(key, task);
		} catch (InterruptedException e) {
			Thread t = Thread.currentThread();
			t.interrupt();
			throw new TimeoutException("Thread interrupt:" + t.getName());
		}
	}
	
	public void execute(K key, final Runnable runnable) throws TimeoutException {
		execute(key, Executors.callable(runnable));
	}

	private <V> V execute(K key, ReentrantFutureTask<V> task) throws InterruptedException, TimeoutException {
		long lastTime = System.currentTimeMillis();
		long remainMillis = timeMillis;
		for (;;) {
			ReentrantFutureTask<?> oldTask = this.taskMap.putIfAbsent(key, task);
			try {
				if (oldTask == null) {
					task.run();
					return task.get(remainMillis, TimeUnit.MILLISECONDS);
				}
				if (oldTask.getRunner() == Thread.currentThread()) {
					task.setUnderControl();
					task.run();
					return task.get(remainMillis, TimeUnit.MILLISECONDS);
				}
			} catch (ExecutionException ex) {
				throw new RuntimeException(ex.getCause());
			}
			// wait
			try {
				oldTask.get(timeMillis, TimeUnit.MILLISECONDS);
			} catch (ExecutionException ex) {
				// ignore
			}

			long current = System.currentTimeMillis();
			remainMillis -= (current - lastTime);
			lastTime = current;
		}
	}

	private class ReentrantFutureTask<V> extends FutureTask<V> {

		private final K key;
		private volatile Thread runner;
		private volatile boolean controller;

		public ReentrantFutureTask(K key, Callable<V> task) {
			super(task);
			this.key = key;
			this.controller = true;
		}

		public ReentrantFutureTask(K key, Runnable task) {
			super(task, null);
			this.key = key;
			this.controller = true;
		}

		public void setUnderControl() {
			this.controller = false;
		}

		@Override
		public void run() {
			try {
				if (runner == null) {
					this.runner = Thread.currentThread();
				}
				super.run();
			} finally {
				if (controller) {
					ExclusiveTaskImpl.this.taskMap.remove(key);
				}
			}
		}

		public Thread getRunner() {
			return this.runner;
		}

	}
}
