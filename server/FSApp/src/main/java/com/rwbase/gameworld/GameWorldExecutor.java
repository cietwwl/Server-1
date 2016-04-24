package com.rwbase.gameworld;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.log.EngineLogger;
import com.rwbase.common.PlayerTaskListener;
import com.rwbase.dao.gameworld.GameWorldAttributeData;
import com.rwbase.dao.gameworld.GameWorldDAO;

/**
 * <pre>
 * 游戏世界的抽象，暂时只用于对游戏逻辑功能的支持
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class GameWorldExecutor implements GameWorld {

	private final EngineLogger logger; // 引擎专用logger
	private final ConcurrentHashMap<String, TaskQueue> map; // 记录以角色ID作为主键执行的任务
	private final ThreadPoolExecutor executor; // 游戏逻辑线程池(迟点有时间自己实现)
	private final ThreadPoolExecutor aysnExecutor;
	private volatile ArrayList<PlayerTaskListener> listeners;
	private final int checkNum = 0xF;	
	
	public GameWorldExecutor(int threadSize, EngineLogger logger, int asynThreadSize) {
		this.logger = logger;
		this.map = new ConcurrentHashMap<String, TaskQueue>(threadSize, 0.5f, threadSize);
		this.executor = new ThreadPoolExecutor(threadSize, threadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		this.aysnExecutor = new ThreadPoolExecutor(asynThreadSize, asynThreadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * 执行异步任务
	 * 
	 * @param task
	 */
	public void asynExecute(Runnable task) {
		this.aysnExecutor.execute(task);
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
	public void asyncExecute(String key, PlayerTask task, TaskExceptionHandler handler) {
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

	/**
	 * <pre>
	 * 异步执行指定主键的任务
	 * </pre>
	 * 
	 * @param key
	 * @param task
	 */
	public void asyncExecute(String key, PlayerTask task) {
		asyncExecute(key, task, null);
	}

	class TaskQueue implements Runnable {

		private final String key;
		private final LinkedList<TaskDecoration> taskQueue;
		private boolean removed;
		private boolean submit;

		private TaskQueue(String key) {
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
			Player player = null;
			int count = 0;
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
					// GameWorld不持有Player对象
					if (player == null) {
						PlayerMgr playerMgr = PlayerMgr.getInstance();
						player = playerMgr.find(key);
					}
					// 即使Player为null也执行task
					taskDecoratioin.getTask().run(player);
					// 通知监听玩家任务
					count++;
					if((count & checkNum) == 0){
						notifyTaskListener(player);
					}
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
			if((count & checkNum) != 0){
				notifyTaskListener(player);
			}
		}
	}

	private void notifyTaskListener(Player player) {
		if (GameWorldExecutor.this.listeners != null) {
			for (int i = listeners.size(); --i >= 0;) {
				try {
					PlayerTaskListener listener = listeners.get(i);
					listener.notifyTaskCompleted(player);
				} catch (Throwable t) {
					logger.error("listener notification raised an exception", t);
				}
			}
		}
	}

	@Override
	public String getAttribute(GameWorldKey key) {
		GameWorldAttributeData data = GameWorldDAO.getInstance().get(key.name());
		return data == null ? null : data.getValue();
	}

	@Override
	public boolean updateAttribute(GameWorldKey key, String attribute) {
		GameWorldAttributeData data = GameWorldDAO.getInstance().get(key.name());
		if(data == null){
			data = new GameWorldAttributeData();
		}
		data.setKey(key.name());
		data.setValue(attribute);
		return GameWorldDAO.getInstance().update(data);
	}

	@Override
	public synchronized void registerPlayerDataListener(PlayerTaskListener listener) {
		ArrayList<PlayerTaskListener> list;
		if (listeners == null) {
			list = new ArrayList<PlayerTaskListener>(1);
		} else {
			list = new ArrayList<PlayerTaskListener>(listeners);
		}
		list.add(listener);
		this.listeners = list;
	}

}

class TaskDecoration {

	private final PlayerTask task;
	private final TaskExceptionHandler handler;

	public TaskDecoration(PlayerTask task, TaskExceptionHandler handler) {
		super();
		this.task = task;
		this.handler = handler;
	}

	public PlayerTask getTask() {
		return task;
	}

	public TaskExceptionHandler getHandler() {
		return handler;
	}

}
