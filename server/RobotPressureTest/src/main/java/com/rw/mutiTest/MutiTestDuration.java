package com.rw.mutiTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;

public class MutiTestDuration {

	private static Logger tmpLog = Logger.getLogger("tmpLog");

	// 执行线程数，并发数
	// private static int threadCount = MutiTestAccount.threadCount;

	final private static int totalCount = MutiTestAccount.totalCount;

	private static int start = MutiTestAccount.start;

	private static String preName = MutiTestAccount.preName;

	// 执行间隔 ms
	private static long span = 1000L;

	private static ExecutorService executorService = Executors.newFixedThreadPool(totalCount);

	private static AtomicInteger finishCount = new AtomicInteger(0);
	private static AtomicInteger successCount = new AtomicInteger(0);
	private static AtomicInteger failCount = new AtomicInteger(0);

	final static long duration = 60 * 60 * 1000l;

	final static private AtomicLong timeCost = new AtomicLong();
	final static private AtomicLong maxTimecost = new AtomicLong();
	private static AtomicInteger executeCount = new AtomicInteger(0);

	public static void main(String[] args) throws Exception {

		final List<Robot> robotList = loginRobots();

		final long startTime = System.currentTimeMillis();

		for (final Robot robot : robotList) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {

					try {
						while (startTime + duration > System.currentTimeMillis()) {
							Thread.sleep(1000l);
							// composeInit(robot);
							//long start = System.currentTimeMillis();
							//robot.addGold(50);
							//chat(robot);
							// compose(robot);
							
							action(robot);
							long cost = System.currentTimeMillis() - start;
							timeCost.addAndGet(cost);
							executeCount.incrementAndGet();
							if (maxTimecost.get() < cost) {
								maxTimecost.set(cost);
							}
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
		tmpLog.info("MutiTestDuration all ongoing ; success:" + successCount.get() + " fail:" + failCount.get() + " finishCount:" + finishCount.get() + " avg in ms:" + avgTimeCost + " maxTimecost:" + maxTimecost.get() + " tps:" + (successCount.get() * 1000 / duration));
	}

	private static void action(Robot robot) {
		boolean booleanresult = robot.gainItem(805001);
		if (!booleanresult) {
			System.out.println("GM获取道具失败：" + 805001);
		}
		// lr.think_time(2);

		booleanresult = robot.sellItem(805001);
		if (!booleanresult) {
			System.out.println("出售道具失败：" + 805001);
		}
		//lr.think_time(2);

		robot.addCoin(10000);

		//lr.start_transaction("合成");
		robot.gainItem(703098);
		robot.gainItem(700097);
		booleanresult = robot.equipCompose(700098);

		if (!booleanresult) {
			System.out.println("合成装备失败：" + 700098);
		} 
		//lr.think_time(2);

		if (robot.getChatCount() < 10) {
			//lr.start_transaction("喊话");
			booleanresult = robot.chat("nnnnnnnnnnnnnnnnnnnnnnn");
			if (!booleanresult) {
				System.out.println("喊话失败");
			} 
			robot.setChatCount(robot.getChatCount() + 1);
		}

		booleanresult = robot.addFriend("100100000021");
		if (!booleanresult) {
			System.out.println("添加好友失败");
		} 

		booleanresult = robot.gamble();
		if (!booleanresult) {
			System.out.println("抽卡失败");
		} 
		//lr.think_time(2);

		booleanresult = robot.openAllEmail();
		if (!booleanresult) {
			System.out.println("获取邮件失败");
		}
		//lr.think_time(2);

		booleanresult = robot.addPower(900);
		if (!booleanresult) {
			System.out.println("添加体力失败");
		}

		booleanresult = robot.doPvE();
		if (!booleanresult) {
			System.out.println("PVE执行失败");
		}
		//lr.think_time(2);

		booleanresult = robot.buyRandom();
		if (!booleanresult) {
			System.out.println("商店购买物品失败");
		} 
	}

	private static List<Robot> loginRobots() throws InterruptedException {
		final List<Robot> robotList = new ArrayList<Robot>();
		final AtomicInteger finishLoginCount = new AtomicInteger();

		ExecutorService loginService = Executors.newFixedThreadPool(totalCount);
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
		tmpLog.info("MutiTestDuration+++++++++++++++++++++++++++++++++ login success:" + robotList.size() + " total:" + totalCount);
		return robotList;
	}

	private static void composeInit(Robot robot) {
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

	// 注册创建角色
	private static void chat(Robot robot) {
		if (robot.chat("搞什么飞机，发那么多消息过来，")) {
			successCount.incrementAndGet();
		} else {
			failCount.incrementAndGet();
		}
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
