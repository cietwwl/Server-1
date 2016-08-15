package com.rwbase.gameworld;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.common.TaskExceptionHandler;
import com.rw.fsutil.concurrent.ParametricTask;
import com.rw.fsutil.concurrent.QueuedTaskExecutor;
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
	private final ThreadPoolExecutor aysnExecutor;
	private volatile ArrayList<PlayerTaskListener> listeners;
	private QueuedTaskExecutor<String, Player> queuedTaskExecutor;
	private QueuedTaskExecutor<String, Void> createExecutor;


	public GameWorldExecutor(int threadSize, EngineLogger logger, int asynThreadSize) {
		this.logger = logger;
		this.listeners = new ArrayList<PlayerTaskListener>(0);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threadSize, threadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new SimpleThreadFactory("player"));
		this.aysnExecutor = new ThreadPoolExecutor(asynThreadSize, asynThreadSize, 120, TimeUnit.SECONDS, 
				new LinkedBlockingQueue<Runnable>(8192), new SimpleThreadFactory("aysn_logic"),new ThreadPoolExecutor.CallerRunsPolicy());
		this.queuedTaskExecutor = new QueuedTaskExecutor<String, Player>(threadSize, logger, executor) {

			@Override
			protected Player tryFetchParam(String key) {
				return PlayerMgr.getInstance().find(key);
			}

			@Override
			protected void afterExecute(String key, Player player) {
				for (int i = listeners.size(); --i >= 0;) {
					try {
						PlayerTaskListener listener = listeners.get(i);
						listener.notifyTaskCompleted(player);
					} catch (Throwable t) {
						GameWorldExecutor.this.logger.error("listener notification raised an exception:"+key, t);
					}
				}
			}
		};
		ThreadPoolExecutor accountExecutor = new ThreadPoolExecutor(threadSize, threadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new SimpleThreadFactory("account"));
		this.createExecutor = new QueuedTaskExecutor<String, Void>(threadSize, logger, accountExecutor) {

			@Override
			protected Void tryFetchParam(String key) {
				return null;
			}

			@Override
			protected void afterExecute(String key, Void param) {
			}
		};
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
	 * 执行账号相关的任务
	 * 
	 * @param accountId
	 * @param task
	 */
	public void executeAccountTask(String accountId, final Runnable task) {
		createExecutor.asyncExecute(accountId, new ParametricTask<Void>() {

			@Override
			public void run(Void e) {
				task.run();
			}
		});
	}

	/**
	 * 获取某个账号当前正在执行的任务数量
	 * 
	 * @param accountId
	 * @return
	 */
	public int getAccountTaskCount(String accountId) {
		return createExecutor.getTaskCount(accountId);
	}

	/**
	 * 获取某个玩家当前执行执行的任务数量
	 * @param userId
	 * @return
	 */
	public int getPlayerTaskCount(String userId) {
		return this.queuedTaskExecutor.getTaskCount(userId);
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
		queuedTaskExecutor.asyncExecute(key, task, handler);
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
		queuedTaskExecutor.asyncExecute(key, task, null);
	}

	@Override
	public String getAttribute(GameWorldKey key) {
		GameWorldAttributeData data = GameWorldDAO.getInstance().get(key.name());
		return data == null ? null : data.getValue();
	}

	@Override
	public boolean updateAttribute(GameWorldKey key, String attribute) {
		GameWorldAttributeData data = GameWorldDAO.getInstance().get(key.name());
		if (data == null) {
			data = new GameWorldAttributeData();
		}
		data.setKey(key.name());
		data.setValue(attribute);
		return GameWorldDAO.getInstance().update(data);
	}

	@Override
	public synchronized void registerPlayerDataListener(PlayerTaskListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		ArrayList<PlayerTaskListener> list = new ArrayList<PlayerTaskListener>(listeners.size() + 1);
		list.addAll(listeners);
		list.add(listener);
		this.listeners = list;
	}

}
