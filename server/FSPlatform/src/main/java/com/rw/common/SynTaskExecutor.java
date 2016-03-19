package com.rw.common;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.common.SimpleThreadFactory;

public class SynTaskExecutor {
	
	private static ExecutorService synTaskService = Executors.newScheduledThreadPool(10,new SimpleThreadFactory("synTaskService"));
	
	private static BlockingDeque<SynTask> taskQueue = new LinkedBlockingDeque<SynTask>();
	
	public static void submitTask(SynTask task){
		
		taskQueue.add(task);
	}
	
	public static void init(){
		for (int i = 0; i < 10; i++) {
			synTaskService.submit(new Runnable() {
				
				@Override
				public void run() {
					doTask();
					
				}
			});
		}
		
	}
	
	private static void doTask(){
		while (true) {
			SynTask task = null;
			try {
				task = taskQueue.poll(10, TimeUnit.SECONDS);
				if(task!=null){
					task.dotask();
				}
			} catch (Throwable e) {
				//do nothing
			}
		}
	}
	
	
}
