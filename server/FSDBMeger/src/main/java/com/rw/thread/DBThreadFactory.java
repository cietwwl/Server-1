package com.rw.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DBThreadFactory {
	private ThreadPoolExecutor executor;
	
	private static DBThreadFactory instance = new DBThreadFactory();
	
	public static DBThreadFactory getInstance(){
		if(instance == null){
			instance = new DBThreadFactory();
		}
		return instance;
	}

	public synchronized void init(int threadSize) {

		executor = new ThreadPoolExecutor(threadSize, threadSize, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(8192), new SimpleThreadFactory("aysn_logic"), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void asynExecute(Runnable task) {
		this.executor.execute(task);
	}
}
