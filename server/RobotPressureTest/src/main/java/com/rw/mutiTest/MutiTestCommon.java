package com.rw.mutiTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;

public class MutiTestCommon {

	private static Logger tmpLog = Logger.getLogger("tmpLog");

	// 执行线程数，并发数
	private static int threadCount = MutiTestAccount.threadCount;

	final private static int totalCount = MutiTestAccount.totalCount;

	private static int start = MutiTestAccount.start;

	private static String preName = MutiTestAccount.preName;

	// 执行间隔 ms
	private static long span = 1000L;

	private static ExecutorService executorService = Executors
			.newFixedThreadPool(threadCount);

	private static AtomicInteger finishCount = new AtomicInteger(0);
	private static AtomicInteger successCount = new AtomicInteger(0);
	private static AtomicInteger failCount = new AtomicInteger(0);

	final static private AtomicLong timeCost = new AtomicLong();
	final static private AtomicLong maxTimecost = new AtomicLong();
	private static AtomicInteger executeCount = new AtomicInteger(0);

	public static void main(String[] args) throws Exception {
		int i = 1;
		final List<Robot> robotList = loginRobots();
		if(i == 0) return;
		for (final Robot robot : robotList) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						while (executeCount.get() < totalCount) {
							Thread.sleep(span/10);
							long start = System.currentTimeMillis();
							//boolean success = robot.groupCompSameScene();
							boolean success = robot.executeRandomMethod();
							tmpLog.info("随机测试:" + success);
							long cost = System.currentTimeMillis() - start;
							timeCost.addAndGet(cost);
							if(success) successCount.incrementAndGet();
							else failCount.incrementAndGet();
							if (maxTimecost.get() < cost) {
								maxTimecost.set(cost);
							}
							executeCount.incrementAndGet();
						}
						robot.close();
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						finishCount.incrementAndGet();
					}
				}
			});
			Thread.sleep(1000l);
		}
		while (executeCount.get() < totalCount) {
			Thread.sleep(span);
		}
		long avgTimeCost = timeCost.get() / executeCount.get();
		tmpLog.info("tasks all ongoing ; success:" + successCount.get()
				+ " fail:" + failCount.get() + " finishCount:"
				+ finishCount.get() + " avg in ms:" + avgTimeCost
				+ " maxTimecost:" + maxTimecost.get());
	}

	/**
	 * 登录一批用户
	 * @return
	 * @throws InterruptedException
	 */
	private static List<Robot> loginRobots() throws InterruptedException {
		final List<Robot> robotList = new ArrayList<Robot>();
		final AtomicInteger finishLoginCount = new AtomicInteger();

		ExecutorService loginService = Executors.newFixedThreadPool(threadCount);
		for (int i = 100; i < 200; i++) {
			final int index = i;
			loginService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						String accountId = preName + index;
						Robot robotTmp = loginRobot(accountId);
						if (robotTmp != null) {
							robotList.add(robotTmp);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						finishLoginCount.incrementAndGet();
					}
				}
			});
			Thread.sleep(100l);
		}
//		while(finishLoginCount.get() < threadCount * 3){
//			Thread.sleep(span);
//		}
		Thread.sleep(15000);
		loginService.shutdownNow();
		tmpLog.info("+++++++++++++++++++++++++++++++++ login success:"
				+ robotList.size() + " total:" + threadCount);
		return robotList;
	}

	//登录角色
	private static Robot loginRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.loginPlatform()) {
			if (robot.loginGame()) {
				return robot;
			}
		}
		robot.close();
		return null;
	}
	
	// 注册创建角色
	private static Robot createRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.regPlatform()) {
			if (robot.creatRole()) {
				return robot;
			}
		}
		return null;
	}
	
	// 修改角色数据
	private static Robot setRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.loginPlatform()) {
			if (robot.loginGame()) {
				Random rd = new Random();
				robot.upgrade(rd.nextInt(10) + 1);
//				robot.addCoin(10000);
//				robot.addGold(10000);
				//robot.applyGroup("10016");
				return robot;
			}
		}
		return null;
	}
}
