package com.rw.mutiTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;
import com.rw.RobotVuser;

public class MutiTestVUser {
	
	private static Logger tmpLog = Logger.getLogger("tmpLog");
	
	//执行线程数，并发数
	private static int threadCount = MutiTestAccount.threadCount;
	
	final private static int totalCount = MutiTestAccount.totalCount;
	
	private static int start = MutiTestAccount.start;
	
	private static String preName = MutiTestAccount.preName;
		
	//执行间隔 ms
	private static long span = 1000L;
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
	
	private static AtomicInteger finishCount = new AtomicInteger(0);
	private static AtomicInteger successCount = new AtomicInteger(0);
	private static AtomicInteger failCount = new AtomicInteger(0);
	
	final static long duration = 10*60*1000l;
	
	
	final static private AtomicLong timeCost = new AtomicLong();
	final static private AtomicLong maxTimecost = new AtomicLong();
	private static AtomicInteger executeCount = new AtomicInteger(0);
	
	
	public static void main(String[] args) throws Exception {
		
		final List<RobotVuser> robotList = loginRobots();
		
		final long startTime = System.currentTimeMillis();
		
		for (final RobotVuser vUser : robotList) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					
					try {
						while(startTime + duration > System.currentTimeMillis()){
							Thread.sleep(1000l);
							long start = System.currentTimeMillis();
//							vUser.runRandom();
							long cost = System.currentTimeMillis()-start;
							timeCost.addAndGet(cost);
							executeCount.incrementAndGet();
							if(maxTimecost.get() < cost){
								maxTimecost.set(cost);
							}
						}
						vUser.close();
						
					} catch (Throwable e) {
						e.printStackTrace();
					}finally{
						finishCount.incrementAndGet();
					}
				}
			});
			Thread.sleep(100l);
		}
		
		while(finishCount.get() < totalCount){
			Thread.sleep(span);
		}
		long avgTimeCost = timeCost.get()/executeCount.get();
		tmpLog.info("tasks all ongoing ; success:"+successCount.get()+" fail:"+failCount.get()+" finishCount:"+finishCount.get()+" avg in ms:"+avgTimeCost+" maxTimecost:"+maxTimecost.get());
	}


	private static List<RobotVuser> loginRobots() throws InterruptedException {
		final List<RobotVuser> robotList = new ArrayList<RobotVuser>();
		final AtomicInteger finishLoginCount = new AtomicInteger();
		for (int i = start; i < start+ totalCount; i++) {
			
			final int index = i;
			executorService.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						String accountId = preName+index;
						Robot robotTmp = loginRobot(accountId);
						if(robotTmp!=null){
//							robotList.add(new RobotVuser(robotTmp));
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}finally{
						finishLoginCount.incrementAndGet();
					}
				}
			});
			Thread.sleep(100l);
		}
		
		while(finishLoginCount.get()<totalCount){
			Thread.sleep(1000L);
		}
		tmpLog.info("+++++++++++++++++++++++++++++++++ login success:"+robotList.size()+" total:"+totalCount);
		return robotList;
	}

	
	private static Robot loginRobot(String accountId){
		Robot robot = Robot.newInstance(accountId);
		
		if(robot.loginPlatform()){
			if(robot.loginGame()){
				return robot;
			}
		}
		robot.close();
		return null;
	}


}
