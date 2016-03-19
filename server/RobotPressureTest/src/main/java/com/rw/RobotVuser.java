package com.rw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class RobotVuser {

	private Robot robot;
	
	private Map<TaskType, Task> taskMap = new HashMap<TaskType, Task>();
	
	
	public boolean regAndCreateRole(){
		return doTimeWrapedTask(TaskType.regPlatform) 
				&& doTimeWrapedTask(TaskType.createRole);
	}
	
	public boolean login(){
		return doTimeWrapedTask(TaskType.loginPlatform) 
				&& doTimeWrapedTask(TaskType.loginGame);
	}
	
	public boolean prepareForPvp(){
		return  doTimeWrapedTask(TaskType.upgradeForPvp) 
				&& doTimeWrapedTask(TaskType.selectCareerForPvp);
		
	}
	
	public void doTaskSequeue(List<TaskType> typeList, long opSpan) throws InterruptedException{
		if(robot == null){
			return;
		}
		
		for (TaskType taskType : typeList) {
			Thread.sleep(opSpan);
			doTimeWrapedTask(taskType);
		}
		
	}
	
	private boolean doTimeWrapedTask(TaskType taskType){
		
		System.out.println("++++++++++++++++++++++++  doTimeWrapedTask + taskType:"+taskType);
		Task task = taskMap.get(taskType);
		if(task.cando()){
			long start = System.currentTimeMillis();
			
			boolean success = task.doTask();
			
			long cost = System.currentTimeMillis() - start;
			
			RobotVuserTimeCost.addCost(success, task.getType(), cost);
			return success;
		}else{
			return true;
		}
	}
	

	public RobotVuser(String accountIdP){
		robot = Robot.newInstance(accountIdP);

		taskMap.put(TaskType.regPlatform, new Task() {
			@Override
			public boolean doTask() {
				return robot.regPlatform();
			}
			@Override
			public TaskType getType() {
				return TaskType.regPlatform;
			}
		});
		taskMap.put(TaskType.createRole, new Task() {
			@Override
			public boolean doTask() {
				return robot.creatRole();
			}
			@Override
			public TaskType getType() {
				return TaskType.createRole;
			}
		});
		taskMap.put(TaskType.loginGame, new Task() {
			@Override
			public boolean doTask() {
				return robot.loginGame();
			}
			@Override
			public TaskType getType() {
				return TaskType.loginGame;
			}
		});
		taskMap.put(TaskType.loginPlatform, new Task() {
			@Override
			public boolean doTask() {
				return robot.loginPlatform();
			}
			@Override
			public TaskType getType() {
				return TaskType.loginPlatform;
			}
		});
		taskMap.put(TaskType.addCoinForGamble, new Task() {
			@Override
			public boolean doTask() {
				return robot.addCoin(10000);
			}
			@Override
			public TaskType getType() {
				return TaskType.addCoinForGamble;
			}
		});
		taskMap.put(TaskType.gamble, new Task() {
			@Override
			public boolean doTask() {
				return robot.gamble();
			}
			@Override
			public TaskType getType() {
				return TaskType.gamble;
			}
		});
		taskMap.put(TaskType.pve, new Task() {
			@Override
			public boolean doTask() {
				return robot.doPvE();
			}
			@Override
			public TaskType getType() {
				return TaskType.pve;
			}
		});
		taskMap.put(TaskType.upgradeForPvp, new Task() {
			@Override
			public boolean doTask() {
				return robot.upgrade(60);
			}
			@Override
			public TaskType getType() {
				return TaskType.upgradeForPvp;
			}
		});
		taskMap.put(TaskType.selectCareerForPvp, new Task() {
			@Override
			public boolean doTask() {
				return robot.selectCarrer();
			}
			@Override
			public TaskType getType() {
				return TaskType.selectCareerForPvp;
			}
		});
		taskMap.put(TaskType.chat, new Task() {
			@Override
			public boolean doTask() {
				return robot.chat("搞什么飞机，发那么多消息过来，");
			}
			@Override
			public TaskType getType() {
				return TaskType.chat;
			}
		});
		taskMap.put(TaskType.pvp,new Task() {
			
			private long nextDoTime = 0;
			private int count = 0;
			private long pvpSpan = 5*60*1000;
			private int dayMax = 5;
			
			@Override
			public boolean doTask() {
				count++;
				nextDoTime = System.currentTimeMillis()+pvpSpan;
				return robot.doPvP();
			}
			@Override
			public TaskType getType() {
				return TaskType.pvp;
			}
			@Override
			public boolean cando() {
				return count < dayMax && nextDoTime < System.currentTimeMillis();
			}
		});
		taskMap.put(TaskType.addCoinForCompose, new Task() {
			@Override
			public boolean doTask() {
				return robot.addCoin(20000);
			}

			@Override
			public TaskType getType() {
				return TaskType.addCoinForCompose;
			}
		});
		taskMap.put(TaskType.equipCompose, new Task() {
			@Override
			public boolean doTask() {
				return robot.equipCompose(700098);
			}

			@Override
			public TaskType getType() {
				return TaskType.equipCompose;
			}
		});
		taskMap.put(TaskType.gainItemForCompose, new Task() {
			@Override
			public boolean doTask() {
				return robot.gainItem(703098) && robot.gainItem(700097);
			}

			@Override
			public TaskType getType() {
				return TaskType.gainItemForCompose;
			}
		});
		
	}
	


	private abstract class Task{
		public abstract boolean doTask();
		public abstract TaskType getType();
		public boolean cando(){
			return true;
		}
	}
	
	public enum TaskType{
		loginPlatform,
		loginGame,
		regPlatform,
		createRole,
		addCoinForGamble,
		upgradeForPvp,
		selectCareerForPvp,
		chat,
		gamble,
		pve,
		pvp,
		addCoinForCompose,
		gainItemForCompose,
		equipCompose;
	}
	
	public void close(){
		robot.close();
	}
	
	public static class RobotVuserTimeCost {

		private static Map<TaskType, AtomicLong> timeMap = new HashMap<TaskType, AtomicLong>();
		
		private static Map<TaskType, AtomicLong> maxTimeMap = new HashMap<TaskType, AtomicLong>();
		
		private static Map<TaskType, AtomicLong> countMap = new HashMap<TaskType, AtomicLong>();
		
		private static Map<TaskType, AtomicLong> successMap = new HashMap<TaskType, AtomicLong>();
		
		public static synchronized void  addCost(boolean success, TaskType taskType, long timeCost){
			AtomicLong atomicLong = timeMap.get(taskType);
			if(atomicLong == null){
				timeMap.put(taskType, new AtomicLong());
				maxTimeMap.put(taskType, new AtomicLong());
				countMap.put(taskType, new AtomicLong());
				successMap.put(taskType, new AtomicLong());
			}
			timeMap.get(taskType).addAndGet(timeCost);
			countMap.get(taskType).incrementAndGet();
			if(success){
				successMap.get(taskType).incrementAndGet();
			}
			
			if(maxTimeMap.get(taskType).get() < timeCost){
				maxTimeMap.get(taskType).set(timeCost);
			}
		}
		
		public static synchronized String getCostInfo(){
			Set<TaskType> typeSet = timeMap.keySet();
			StringBuilder infoBuilder = new StringBuilder("\n");
			for (TaskType taskTypeTmp : typeSet) {
				infoBuilder.append(taskTypeTmp);
				
				long successCount = successMap.get(taskTypeTmp).get();
				infoBuilder.append(" successCount:").append(successCount);
				
				
				long totalCount = countMap.get(taskTypeTmp).get();
					infoBuilder.append(" totalCount:").append(totalCount);
				
				if(totalCount > 0 ){
					long avgCost = timeMap.get(taskTypeTmp).get()/totalCount;
					infoBuilder.append(" avgCost:").append(avgCost);
				}
				
				long maxCost = maxTimeMap.get(taskTypeTmp).get();
				infoBuilder.append(" maxCost:").append(maxCost);
				infoBuilder.append("\n");
				
			}
			return infoBuilder.toString();
		}
		
		
	}
	
}
