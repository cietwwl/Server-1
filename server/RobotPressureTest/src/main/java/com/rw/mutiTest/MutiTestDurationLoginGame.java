package com.rw.mutiTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;

public class MutiTestDurationLoginGame {
	
	private static Logger tmpLog = Logger.getLogger("tmpLog");
	
	//执行线程数，并发数
//	private static int threadCount = MutiTestAccount.threadCount;
	
	final private static int totalCount = MutiTestAccount.totalCount;
	
	private static int start = MutiTestAccount.start;
	
	private static String preName = MutiTestAccount.preName;
	//执行间隔 ms
	private static long span = 1000L;

	
	private static ExecutorService executorService = Executors.newFixedThreadPool(totalCount);
	
	private static AtomicInteger finishCount = new AtomicInteger(0);
	private static AtomicInteger successCount = new AtomicInteger(0);
	private static AtomicInteger failCount = new AtomicInteger(0);
	
	final static long duration = 1*60*1000l;
	
	
	final static private AtomicLong timeCost = new AtomicLong();
	final static private AtomicLong maxTimecost = new AtomicLong();
	private static AtomicInteger executeCount = new AtomicInteger(0);
	
	
	public static void main(String[] args) throws Exception {
		
		
		final long startTime = System.currentTimeMillis();
		
		for (int i = start; i < start+ totalCount; i++) {
			final int index = i;
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					String accountId = preName+index;
					final Robot robot = Robot.newInstance(accountId);
					boolean loginPlatformSuccess = robot.loginPlatform();
					try {
						if(loginPlatformSuccess){
							while(startTime + duration > System.currentTimeMillis()){
								Thread.sleep(1000l);
								long start = System.currentTimeMillis();
								
								if(robot.loginGame()){
									successCount.incrementAndGet();
								}else{
									failCount.incrementAndGet();
								}
								
								long cost = System.currentTimeMillis()-start;
								timeCost.addAndGet(cost);
								executeCount.incrementAndGet();
								if(maxTimecost.get() < cost){
									maxTimecost.set(cost);
								}
								robot.close();
							}
						}else{
							failCount.incrementAndGet();
						}
						
					} catch (Throwable e) {
						e.printStackTrace();
					}finally{
						finishCount.incrementAndGet();
					}
				}
			});
//			Thread.sleep(100l);
		}
		
		while(finishCount.get() < totalCount){
			Thread.sleep(span);
		}
		long avgTimeCost = timeCost.get()/executeCount.get();
		tmpLog.info("tasks all ongoing ; success:"+successCount.get()+" fail:"+failCount.get()+" finishCount:"+finishCount.get()+" avg in ms:"+avgTimeCost+" maxTimecost:"+maxTimecost.get()+" tps:"+(successCount.get()*duration/1000));
	}
	



}
