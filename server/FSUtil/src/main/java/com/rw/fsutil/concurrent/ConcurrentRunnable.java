package com.rw.fsutil.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * <pre>
 * 以Key作为互斥条件串行执行的任务
 * 当有相同的Key(调用K的equals()方法返回true)会阻塞，直到任务执行完再尝试执行
 * </pre>
 * 
 * @author Jamaz
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ConcurrentRunnable<K> {

	private final ConcurrentHashMap<K, FutureTask> taskMap;

	public ConcurrentRunnable() {
		this.taskMap = new ConcurrentHashMap<K, FutureTask>();
	}

	public ConcurrentRunnable(int size) {
		this.taskMap = new ConcurrentHashMap<K, FutureTask>(size);
	}

	/**
	 * 执行以Key作为主键互斥的任务
	 * 
	 * @param key
	 * @param runnable
	 */
	public void executeTask(final K key, final Runnable runnable) {
		executeTask(key, runnable, null);
	}

	/**
	 * 执行以Key作为主键互斥的任务
	 * 
	 * @param key
	 * @param runnable
	 * @param executor
	 */
	public void executeTask(final K key, final Runnable runnable, Executor executor) {
		final FutureTask task = new FutureTask(new Callable() {

			@Override
			public Object call() throws Exception {
				try {
					// 执行任务
					runnable.run();
				} finally {
					// 移除这个Key任务
					taskMap.remove(key);
				}
				return null;
			}
		});
		boolean interrupted = false;
		try {
			for (;;) {
				FutureTask old = this.taskMap.putIfAbsent(key, task);
				if (old == null) {
					if (executor != null) {
						executor.execute(task);
					} else {
						task.run();
						try {
							task.get();
						} catch (InterruptedException ex) {
							interrupted = true;
						} catch (ExecutionException ex) {
							throw new RuntimeException(ex);
						}
					}
					break;
				} else {
					try {
						old.get();
					} catch (InterruptedException ex) {
						interrupted = true;
					} catch (ExecutionException ex) {
						// ignore
					}
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * 获取任务
	 * 
	 * @param key
	 * @return
	 */
	public FutureTask getFutureTask(K key) {
		return this.taskMap.get(key);
	}
}
