package com.rw.fsutil.concurrent;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import com.rw.fsutil.common.TaskExceptionHandler;
import com.rw.fsutil.log.EngineLogger;

/**
 * <pre>
 * 用于异步串行执行任务，以主键{@link K}为单位
 * 如果多线程同时提交多个任务，同一个主键{@link K}的任务会被安排按主键一个个串行执行，提交线程无需阻塞
 * </pre>
 * 
 * @author Jamaz
 *
 */
public abstract class QueuedTaskExecutor<K, E> {

	private final EngineLogger logger;
	private final ConcurrentHashMap<K, TaskQueue> map;
	private final Executor executor;

	public QueuedTaskExecutor(int threadSize, EngineLogger logger, Executor executor) {
		this.logger = logger;
		this.map = new ConcurrentHashMap<K, TaskQueue>(threadSize, 0.5f, threadSize);
		this.executor = executor;
	}

	/**
	 * <pre>
	 * 异步执行指定主键的任务
	 * </pre>
	 * 
	 * @param key
	 * @param task
	 * @param handler
	 */
	public void asyncExecute(K key, ParametricTask<E> task, TaskExceptionHandler handler) {
		TaskQueue keyTask = map.get(key);
		TaskDecoration decoratioin = new TaskDecoration(task, handler);
		if (keyTask != null && keyTask.addTask(decoratioin)) {
			return;
		}
		TaskQueue newTask = new TaskQueue(key);
		for (;;) {
			TaskQueue old = map.putIfAbsent(key, newTask);
			if (old == null) {
				if (newTask.addTask(decoratioin)) {
					break;
				}
			} else if (old.addTask(decoratioin)) {
				break;
			}
		}
	}

	public int getTaskCount(String key) {
		TaskQueue keyTask = map.get(key);
		if (key == null) {
			return 0;
		}
		return keyTask.size();
	}

	/**
	 * <pre>
	 * 异步执行指定主键的任务
	 * </pre>
	 * 
	 * @param key
	 * @param task
	 */
	public void asyncExecute(K key, ParametricTask<E> task) {
		asyncExecute(key, task, null);
	}

	class TaskQueue implements Runnable {

		private final K key;
		private final LinkedList<TaskDecoration> taskQueue;
		private boolean removed;
		private boolean submit;

		private TaskQueue(K key) {
			this.key = key;
			this.taskQueue = new LinkedList<TaskDecoration>();
		}

		public boolean addTask(TaskDecoration task) {
			boolean offerExecutor;
			synchronized (this) {
				if (removed) {
					return false;
				}
				taskQueue.add(task);
				if (!submit) {
					submit = true;
					offerExecutor = true;
				} else {
					offerExecutor = false;
				}
			}
			if (offerExecutor) {
				executor.execute(this);
			}
			return true;
		}

		@Override
		public void run() {
			TaskDecoration taskDecoratioin = null;
			E param = null;
			for (;;) {
				synchronized (this) {
					taskDecoratioin = taskQueue.poll();
					if (taskDecoratioin == null) {
						map.remove(key);
						removed = true;
						break;
					}
				}
				try {
					// 若为null，每次都会去尝试获取一次
					if (param == null) {
						param = tryFetchParam(key);
					}
					taskDecoratioin.getTask().run(param);
					// 通知监听玩家任务
					afterExecute(key, param);
				} catch (Throwable t) {
					t.printStackTrace();
					logger.error("A task raised an exception", t);
					TaskExceptionHandler handler = taskDecoratioin.getHandler();
					try {
						if (handler != null) {
							handler.handle(t);
						}
					} catch (Throwable t2) {
						t2.printStackTrace();
						logger.error("exception handler raised an exception", t2);
					}
				}
			}
		}

		public int size() {
			synchronized (this) {
				return taskQueue.size();
			}
		}
	}

	class TaskDecoration {

		private final ParametricTask<E> task;
		private final TaskExceptionHandler handler;

		public TaskDecoration(ParametricTask<E> task, TaskExceptionHandler handler) {
			this.task = task;
			this.handler = handler;
		}

		public ParametricTask<E> getTask() {
			return task;
		}

		public TaskExceptionHandler getHandler() {
			return handler;
		}
	}

	/**
	 * 获取参数
	 * 
	 * @param key
	 * @return
	 */
	protected abstract E tryFetchParam(K key);

	/**
	 * 
	 * @param param
	 */
	protected abstract void afterExecute(K key, E param);

}
