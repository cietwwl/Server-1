package com.rwbase.gameworld;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.concurrent.ParametricTask;
import com.rw.fsutil.concurrent.QueuedTaskExecutor;
import com.rw.fsutil.log.EngineLogger;

public class GameWorldExecutor implements GameWorld {

	private final EngineLogger logger; // 引擎专用logger
	private final ThreadPoolExecutor aysnExecutor;
	
	
	private QueuedTaskExecutor<String, Void> createExecutor;

	public GameWorldExecutor(int threadSize, EngineLogger logger, int asynThreadSize) {
		this.logger = logger;
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threadSize, threadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new SimpleThreadFactory("player"));
		this.aysnExecutor = new ThreadPoolExecutor(asynThreadSize, asynThreadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(8192), new SimpleThreadFactory("aysn_logic"),
				new ThreadPoolExecutor.CallerRunsPolicy());
		
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
}
