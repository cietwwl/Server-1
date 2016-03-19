package com.gm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GmExecutor {

	
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	
	private static GmExecutor instance = new GmExecutor();
	
	public static GmExecutor getInstance(){
		return instance;
	}
	
	public void submit(Runnable task){
		executorService.submit(task);
	}
	
}
