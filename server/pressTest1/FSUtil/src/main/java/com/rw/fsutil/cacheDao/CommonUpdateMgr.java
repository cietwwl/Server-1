package com.rw.fsutil.cacheDao;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.log.SqlLog;


public class CommonUpdateMgr {
	
	private static CommonUpdateMgr instance = new CommonUpdateMgr();
	
	private ScheduledExecutorService scheduledExecutor;
	
	private ConcurrentLinkedQueue<CommonUpdateTask> sqlQueue;

	private boolean flushData = false;
	//每秒最多update的次数
	private final int UpdateThreadHoldInSecond =  100; 
	
	public static CommonUpdateMgr getInstance(){		
		return instance;
	}
	
	private CommonUpdateMgr(){
		
		sqlQueue = new ConcurrentLinkedQueue<CommonUpdateTask>();
		
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		
		Runnable command = new Runnable() {
			
			@Override
			public void run() {	
				if(!flushData){
					try {
						
						updateToDb();
						
					} catch (Throwable throwableP) {						
						SqlLog.error("CommonUpdateMgr[updateToDb]更新到db的时候出错 ", throwableP);
					}
				}
			}
		};
		
		long initalDelay = 0L;
		long period = 1L;
		scheduledExecutor.scheduleAtFixedRate(command , initalDelay , period , TimeUnit.SECONDS);
		
	}
	
	private void updateToDb(){
		int count = 0;
		while(!sqlQueue.isEmpty() && count < UpdateThreadHoldInSecond){
			if(flushData){
				break;
			}
			count++;
			CommonUpdateTask task = sqlQueue.poll();
			if(task!=null){
				task.doTask();
			}			
		}
		
	}
	
	public void addTask(CommonUpdateTask updateTask){
		if(updateTask!=null){			
			sqlQueue.add(updateTask);
		}
	}
	
	//应用关闭的时候flush所有的data
	public void flushData(){
		flushData = true;
		
		while(!sqlQueue.isEmpty()){				
			CommonUpdateTask task = sqlQueue.poll();
			if(task!=null){
				task.doTask();
			}			
		}
		flushData = false;
	}
}
