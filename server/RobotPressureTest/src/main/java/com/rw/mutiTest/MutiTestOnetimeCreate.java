package com.rw.mutiTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;

public class MutiTestOnetimeCreate {

	private static Logger tmpLog = Logger.getLogger("tmpLog");

	// 执行线程数，并发数
	private static int threadCount = MutiTestAccount.threadCount;
	
	final private static int totalCount = MutiTestAccount.totalCount;
	
	private static int start = MutiTestAccount.start;
	
	private static String preName = MutiTestAccount.preName;
	
	// 执行间隔 ms
	private static long span = 1000L;

	final static private AtomicLong timeCost = new AtomicLong();

	private static ExecutorService executorService = Executors
			.newFixedThreadPool(threadCount);

	private static AtomicInteger finishCount = new AtomicInteger(0);
	private static AtomicInteger successCount = new AtomicInteger(0);
	private static AtomicInteger failCount = new AtomicInteger(0);

	private static AtomicLong maxTimeCost = new AtomicLong();
	
	private static boolean withCarrer = false;

	public static void main(String[] args) throws Exception {

		for (int i = start; i < start + totalCount; i++) {

			final int index = i;
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						String accountId = preName + index;
						createRobot(accountId);
					} catch (Throwable e) {
						e.printStackTrace();
					}finally{
						finishCount.incrementAndGet();
					}
				}
			});
			Thread.sleep(100l);
		}

		while (finishCount.get() < totalCount) {
			Thread.sleep(span);
		}
		long avgTimeCost = timeCost.get() / finishCount.get();
		
		tmpLog.info("tasks all ongoing ; success:" + successCount.get()
				+ " fail:" + failCount.get() + " finishCount:"
				+ finishCount.get() + " avg in ms:" + avgTimeCost + " maxTimeCost:"+maxTimeCost.get());
	}

	// 注册创建角色
	private static Robot createRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.regPlatform()) {
			long startTime = System.currentTimeMillis();

			if (robot.creatRole()) {
				if(withCarrer){
					if(robot.upgrade(60) && robot.selectCarrer()){
						successCount.incrementAndGet();
					}else{
						failCount.incrementAndGet();
					}
					
				}else{
					successCount.incrementAndGet();
				}
			} else {
				failCount.incrementAndGet();
			}
			long endTime = System.currentTimeMillis();
			long cost = endTime - startTime;
			timeCost.addAndGet(cost);
			if (cost > maxTimeCost.get()) {
				maxTimeCost.set(cost);
			}
			return robot;
		}else{
			failCount.incrementAndGet();
		}
		return robot;
	}

}
