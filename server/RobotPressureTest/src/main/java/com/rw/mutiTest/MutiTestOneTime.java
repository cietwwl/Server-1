package com.rw.mutiTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;

public class MutiTestOneTime {

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

		final List<Robot> robotList = loginRobots();

		for (final Robot robot : robotList) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {

					try {
						Thread.sleep(1000l);
						composeInit(robot);
						long start = System.currentTimeMillis();
						
						compose(robot);

						long cost = System.currentTimeMillis() - start;
						timeCost.addAndGet(cost);
						executeCount.incrementAndGet();

						if (maxTimecost.get() < cost) {
							maxTimecost.set(cost);
						}
						robot.close();

					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						finishCount.incrementAndGet();
					}
				}
			});
			Thread.sleep(100l);
		}

		while (finishCount.get() < totalCount) {
			Thread.sleep(span);
		}
		long avgTimeCost = timeCost.get() / executeCount.get();
		tmpLog.info("tasks all ongoing ; success:" + successCount.get()
				+ " fail:" + failCount.get() + " finishCount:"
				+ finishCount.get() + " avg in ms:" + avgTimeCost
				+ " maxTimecost:" + maxTimecost.get());
	}

	private static void composeInit(Robot robot){
		robot.addCoin(20000);
		robot.gainItem(703098);
		robot.gainItem(700097);
	}
	
	// 注册创建角色
	private static void compose(Robot robot) {
		if (robot.equipCompose(700098)) {
			successCount.incrementAndGet();
		} else {
			failCount.incrementAndGet();
		}
	}

	// 注册创建角色
	private static void gamble(Robot robot) {
		if (robot.gamble()) {
			successCount.incrementAndGet();
		} else {
			failCount.incrementAndGet();
		}
	}

	private static List<Robot> loginRobots() throws InterruptedException {
		final List<Robot> robotList = new ArrayList<Robot>();
		final AtomicInteger finishLoginCount = new AtomicInteger();

		ExecutorService loginService = Executors
				.newFixedThreadPool(threadCount);
		for (int i = start; i < start + totalCount; i++) {

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

		while (finishLoginCount.get() < totalCount) {
			Thread.sleep(1000L);
		}
		loginService.shutdownNow();
		tmpLog.info("+++++++++++++++++++++++++++++++++ login success:"
				+ robotList.size() + " total:" + totalCount);
		return robotList;
	}

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

}
