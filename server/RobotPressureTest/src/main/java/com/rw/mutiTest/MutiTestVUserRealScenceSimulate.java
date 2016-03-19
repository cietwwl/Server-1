package com.rw.mutiTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.rw.RobotVuser;
import com.rw.RobotVuser.TaskType;

public class MutiTestVUserRealScenceSimulate {
	
	private static Logger tmpLog = Logger.getLogger("tmpLog");
	
	//执行线程数，并发数
	private static int threadCount = 1000;
	
	private static int start = 0;
	
	private static String preName = "vUser101";
	
	private static boolean userOldUserLogin = true;

	//操作间隔
	final static long opSpan = 1000;
	//用户在线时间
	final static long vuserOnlineTime = 60*1000; 
	//一分钟注册数或者登陆数（userOldUserLogin=true）
	final static int regOrLoginInminute = 1000;
	//最大注册人数
	final static int maxReg = 5000;
	
	final static long duration = 2*60*60*1000l;
	
	final static LinkedBlockingQueue<RobotVuser> vUserQueue = new LinkedBlockingQueue<RobotVuser>();
	
	
	final static ExecutorService mainService = Executors.newFixedThreadPool(2);
	
	final static ExecutorService onlineService = Executors.newFixedThreadPool(threadCount);
	
	final static ExecutorService regService = Executors.newFixedThreadPool(500);
	
	final static ExecutorService loginService = Executors.newFixedThreadPool(500);
	
	final static AtomicBoolean shutdown = new AtomicBoolean(false);
	
	public static void main(String[] args) throws Exception {
		
		long existTime = System.currentTimeMillis() + duration;
		if(userOldUserLogin){
			loginRobots();
		}else{
			createRobots();
		}
		
		onlineRobots();
		
		long lastTmpTime = System.currentTimeMillis();
		
		while(existTime > System.currentTimeMillis()){
			Thread.sleep(1000);
			System.out.println("++++++++++++++++++++++++++++++MutiTestVUserRealScenceSimulate on going...");
			if(System.currentTimeMillis() > lastTmpTime+10*60*1000){
				lastTmpTime = System.currentTimeMillis();
				tmpLog.info(RobotVuser.RobotVuserTimeCost.getCostInfo());
			}
		}
		shutdown.set(true);
		onlineService.shutdownNow();
		regService.shutdownNow();
		
		while(!onlineService.isTerminated() || !regService.isTerminated()){
			Thread.sleep(1000);
			System.out.println("++++++++++++++++++++++++++++++MutiTestVUserRealScenceSimulate shutdown ....");
		}
		
		tmpLog.info(RobotVuser.RobotVuserTimeCost.getCostInfo());
	}



	private static List<TaskType> getUserOpList() {
		
		List<TaskType> opList = new ArrayList<TaskType>();
		
		opList.add(TaskType.addCoinForGamble);
		opList.add(TaskType.gamble);
		opList.add(TaskType.pve);
		opList.add(TaskType.chat);
		opList.add(TaskType.pvp);
		opList.add(TaskType.addCoinForCompose);
		opList.add(TaskType.gainItemForCompose);
		opList.add(TaskType.equipCompose);
		
		return opList;
	}
		

	private static void createRobots() throws InterruptedException {
		
		mainService.submit(new Runnable() {
			
			@Override
			public void run() {
				
				long nextRegTime = 0;
				int timeSpanMs = 60*1000/regOrLoginInminute;
				AtomicInteger index = new AtomicInteger(start);
				final AtomicInteger regCount = new AtomicInteger(0);
				
				while(true){
					if(shutdown.get() || regCount.get() > maxReg){
						break;
					}
					try {
						
						if(System.currentTimeMillis() > nextRegTime){
							nextRegTime = System.currentTimeMillis() + timeSpanMs;
							final String accountId = preName + index.incrementAndGet();
							System.out.println("reg submit accountId:"+accountId);
							regService.submit(new Runnable() {
								@Override
								public void run() {
									try {
										RobotVuser robotVuser = new RobotVuser(accountId);
										if(robotVuser.regAndCreateRole()){
											vUserQueue.add(robotVuser);
											regCount.incrementAndGet();
											System.out.println("reg success+++");
										}else{
											System.out.println("reg fail+++");
										}
									} catch (Throwable e) {
										e.printStackTrace();
									}
									
								}
							});
							
						}
						
						Thread.sleep(10);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					
				}
				
			}
		});
		
		
	}
	private static void loginRobots() throws InterruptedException {
		
		mainService.submit(new Runnable() {
			
			@Override
			public void run() {
				
				long nextRegTime = 0;
				int timeSpanMs = 60*1000/regOrLoginInminute;
				AtomicInteger index = new AtomicInteger(start);
				final AtomicInteger loginCount = new AtomicInteger(0);
				
				while(true){
					if(shutdown.get() || loginCount.get() > maxReg){
						break;
					}
					try {
						
						if(System.currentTimeMillis() > nextRegTime){
							nextRegTime = System.currentTimeMillis() + timeSpanMs;
							final String accountId = preName + index.incrementAndGet();
							System.out.println("reg submit accountId:"+accountId);
							loginService.submit(new Runnable() {
								@Override
								public void run() {
									try {
										RobotVuser robotVuser = new RobotVuser(accountId);
										if(robotVuser.login()){
											vUserQueue.add(robotVuser);
											loginCount.incrementAndGet();
											System.out.println("login success+++");
										}else{
											System.out.println("login fail+++");
										}
									} catch (Throwable e) {
										e.printStackTrace();
									}
									
								}
							});
							
						}
						
						Thread.sleep(10);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					
				}
				
			}
		});
		
		
	}

	private static void onlineRobots() throws InterruptedException {
		
		final List<TaskType> typeList = getUserOpList();
		for (int i = 0; i < threadCount; i++) {
			onlineService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						long offlineTime = 0;
						RobotVuser vUser = null; 
						while(true){
							if(shutdown.get()){
								break;
							}
							if(System.currentTimeMillis() > offlineTime){
								RobotVuser newRegUser = vUserQueue.poll(1, TimeUnit.SECONDS);
								if(newRegUser!=null){
									if(vUser!=null){
										vUser.close();
									}
									vUser = newRegUser;
									offlineTime = System.currentTimeMillis() + vuserOnlineTime;
									vUser.prepareForPvp();
									newRegUser.doTaskSequeue(typeList, opSpan);
								}else if(vUser!=null){
									vUser.doTaskSequeue(typeList, opSpan);
								}
								
							}else{
								Thread.sleep(10);
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					
				}
			});
			
		}
			
	}





}
