package com.rw.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;


/***
 * 保证对用户的操作不会重复
 * @author allen
 *
 */
public class GamePlayerOpHelper {

	private boolean ongoing = false;
	
	private AtomicInteger finishCount = new AtomicInteger(0);
	
	private int totalToDo = 0;
	
	private List<PlayerTask> callBackList = new ArrayList<PlayerTask>();
	
	private ExecutorService executorService;
	
	private ExecutorService monitorService;	
	
	public GamePlayerOpHelper(int workThreads){
		executorService = Executors.newFixedThreadPool(workThreads);
		monitorService = Executors.newFixedThreadPool(1);
		 
	}
	
	public int getProgress(){
		int progress = 100;
		if(totalToDo>0){
			progress = finishCount.get()*100/totalToDo;
		}
		
		return progress;
	}
	
	public synchronized int addTask(final List<Player> playerList, PlayerTask playerTask){
		int progress = 0;
		if(ongoing){
			progress =  getProgress();
			if(!callBackList.contains(playerTask)){
				callBackList.add(playerTask);
			}
		}else{
			
			if(!callBackList.contains(playerTask)){
				callBackList.add(playerTask);
			}
			final List<PlayerTask> tempList = callBackList;
			callBackList = new ArrayList<PlayerTask>();
			ongoing = true;
			monitorService.submit(new Runnable() {
				@Override
				public void run() {
					doTask(playerList, tempList);
				}
			});
			
		}
		return progress;
	}

	private void doTask(List<Player> playerList, List<PlayerTask> tempList) {
		try {				
			finishCount.set(0);	
			totalToDo = playerList.size()*tempList.size();
			for (PlayerTask playerCallBack : tempList) {
				doSingleTask(playerList, playerCallBack);
			}
		} finally {
			ongoing = false;
		}
	}
	
	private void doSingleTask(final List<Player> playerList, final PlayerTask task) {
		long start = System.currentTimeMillis();
		final int taskNum = playerList.size();
		final AtomicInteger taskfinishCount = new AtomicInteger(0);
		for (final Player player : playerList) {
			
			executorService.submit(new Runnable() {
				
				@Override
				public void run() {
						try {
							if(player!=null){
								if(task!=null){
									task.doCallBack(player);
								}
							}
						} catch (Throwable e) {
							GameLog.error(LogModule.COMMON.getName(), player.getUserId(), "GamePlayerOpHelper[doSingleTask] 用户数据操作错误 task："+task.getName(), e);
						}finally{
							taskfinishCount.incrementAndGet();
							finishCount.incrementAndGet();
						}
					}
					
			});
			
		}		
		
		while(taskfinishCount.get() < taskNum){
			try {
				GameLog.info(LogModule.COMMON.getName(), task.getName(), "GamePlayerOpHelper[doSingleTask] 用户数据操作进行中。。。task:"+task.getName()+" finishCount:"+taskfinishCount+" total:"+taskNum, null);
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				//donothing
			}
		}
		String timeCost = " time cost in ms:"+(System.currentTimeMillis()-start);
		GameLog.info(LogModule.COMMON.getName(), task.getName(), "GamePlayerOpHelper[doSingleTask] 用户数据操作完成。。。task:"+task.getName()+" finishCount:"+taskfinishCount+" total:"+taskNum+ timeCost, null);
		
	}
	
}
