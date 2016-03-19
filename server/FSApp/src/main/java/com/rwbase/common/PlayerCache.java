package com.rwbase.common;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.playerdata.Player;
import com.rw.fsutil.log.EngineLogger;
import com.rw.fsutil.log.EngineLoggerFactory;

public class PlayerCache {

	private EngineLogger logger;
	private final ReentrantLock lock = new ReentrantLock();
	private LinkedHashMap<String, Player> cache;
	private ConcurrentHashMap<String, ReentrantFutureTask> taskMap;
	private final long timeoutNanos;

	public PlayerCache(final int maxCapacity,int timeoutSeconds) {
		if (maxCapacity <= 1) {
			throw new IllegalArgumentException("maxCapacity <= 1");
		}
		this.timeoutNanos = TimeUnit.SECONDS.toNanos(timeoutSeconds);
		this.logger = EngineLoggerFactory.getLogger("playerCache");
		cache = new LinkedHashMap<String, Player>(maxCapacity, 0.5f, true) {

			@Override
			protected boolean removeEldestEntry(Entry<String, Player> eldest) {
				if (size() > maxCapacity) {
					
				}
				return false;
			}
		};
	}

	private ReentrantFutureTask submitKeyTask(String key, ReentrantFutureTask task) throws InterruptedException, TimeoutException {
		ReentrantFutureTask otherTask = this.taskMap.get(key);
		if (otherTask == null) {
			otherTask = this.taskMap.putIfAbsent(key, task);
			// 获取了任务的执行权
			if (otherTask == null) {
				logger.info("add task：" + key + "," + task);
				return null;
			}
		}

		// 如果该任务是由当前线程执行，直接返回
		if (otherTask.getRunner() == Thread.currentThread()) {
			logger.warn("run in same thread：" + otherTask + "," + task);
			task.setUnderControl();
			return null;
		}

		// 这里执行其他操作可能会加载到内存
		try {
			otherTask.get(timeoutNanos, TimeUnit.SECONDS);
		} catch (ExecutionException ex) {
			// ignore
		} catch (TimeoutException e) {
			logger.error("等待超时：" + otherTask, e);
			throw e;
		}
		return otherTask;
	}

	abstract class TaskCallable<V> implements Callable<V> {

		final String key;

		public TaskCallable(String key) {
			this.key = key;
		}

		public abstract String getName();
	}

	private class ReentrantFutureTask<V> extends FutureTask<V> {

		private final TaskCallable<V> task;
		private volatile Thread runner;
		private volatile boolean controller;

		public ReentrantFutureTask(TaskCallable<V> task) {
			super(task);
			this.task = task;
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
					Future old = PlayerCache.this.taskMap.remove(task.key);
					if (old != this) {
						logger.error("remove other task, current = " + task + ",old = " + old);
					}
				}
			}
		}

		public Thread getRunner() {
			return this.runner;
		}

		public String toString() {
			return task.getName();
		}

	}
}
