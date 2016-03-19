package com.bm.rank;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.log.GameLog;


public class RankDataMgr {
	
	private static RankDataMgr instance = new RankDataMgr();
	
	private ScheduledExecutorService scheduledExecutor;
	
	private ConcurrentLinkedQueue<IRankDbTask> sqlQueue;

	private boolean flushData = false;
	//每秒最多update的次数
	private final int UpdateThreadHoldInSecond =  100; 
	
	public static RankDataMgr getInstance(){		
		return instance;
	}
	
	private RankDataMgr(){
		
		sqlQueue = new ConcurrentLinkedQueue<IRankDbTask>();
		
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		
		Runnable command = new Runnable() {
			
			@Override
			public void run() {	
				if(!flushData){
					try {
						
						updateToDb();
						
					} catch (Throwable throwableP) {						
						GameLog.error("RankDataMgr", "RankDataMgr[updateToDb]", "更新到db的时候出错", throwableP);
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
			IRankDbTask task = sqlQueue.poll();
			if(task!=null){
				task.doTask();
			}			
		}
		
	}
	
	public void addTask(IRankDbTask rankDbTask){
		if(rankDbTask!=null){			
			sqlQueue.add(rankDbTask);
		}
	}
	
	//应用关闭的时候flush所有的data
	public void flushData(){
		flushData = true;
		
		while(!sqlQueue.isEmpty()){				
			IRankDbTask task = sqlQueue.poll();
			if(task!=null){
				task.doTask();
			}			
		}
		flushData = false;
	}
}
