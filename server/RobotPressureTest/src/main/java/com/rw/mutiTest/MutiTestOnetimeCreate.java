package com.rw.mutiTest;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;
import com.rw.Test;

public class MutiTestOnetimeCreate {

	private static Logger tmpLog = Logger.getLogger("tmpLog");
	/**线程和总数同步参数*/
	private static int num = 1;
	
	// 执行线程数，并发数
	private static int threadCount = MutiTestAccount.threadCount/num;

	final private static int totalCount = MutiTestAccount.totalCount/num;

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
						
//						long start = System.currentTimeMillis();
//						String accountId = preName + index;
//						System.out.println("!!!!!!!!!!!!!!!!!!!begin.name="+ accountId);
//						Robot robot = createRobot(accountId);
//						long now = System.currentTimeMillis();
//						long spend = now - start;
//						long all = spend;
//						System.out.println("~~~~~~~~~~~~~~~~~~~~creatrole="+ spend + "   all = " + all);
//						Robot robot = Test.loginRobot(accountId);
//						robot.checkEnoughMoney();						
//						long tmp = now;
//						now = System.currentTimeMillis();
//						spend = now - tmp;
//						all = spend + all;
//						System.out.println("~~~~~~~~~~~~~~~~~~~~add="+ spend+ "   all = " + all);
////						robot.upgrade(50);
//						tmp = now;
//						now = System.currentTimeMillis();
//						spend = now - tmp;
//						all = spend + all;
//						System.out.println("~~~~~~~~~~~~~~~~~~~~upgrade="+ spend+ "   all = " + all);
//						robot.addHero(5);
//						tmp = now;
//						now = System.currentTimeMillis();
//						spend = now - tmp;
//						all = spend + all;
//						System.out.println("~~~~~~~~~~~~~~~~~~~~hero="+ spend+ "   all = " + all);
//						robot.createGroup(accountId);
//						tmp = now;
//						now = System.currentTimeMillis();
//						spend = now - tmp;
//						all = spend + all;
//						System.out.println("~~~~~~~~~~~~~~~~~~~~creatgroup="+ spend+ "   all = " + all);
//						robot.createGroupSecret();
//						tmp = now;
//						now = System.currentTimeMillis();
//						spend = now - tmp;
//						all = spend + all;
//						System.out.println("~~~~~~~~~~~~~~~~~~~~creatgroupsecret="+ spend+ "   all = " + all);
//						for (int i = 0; i < 5; i++) {
//							int normolEquipType = Test.random.nextInt(5);
//							normolEquipType = normolEquipType == 0 ? 1
//									: normolEquipType;
//							boolean issucc = robot.testFixEquip(0, 0, 1,
//									normolEquipType);
//							System.out.println(i + "@@@@@@@" + issucc
//									+ "         " + normolEquipType  + "    " +accountId);
//						}
//						for (int i = 0; i < 4; i++) {
//							int expEquipType = Test.random.nextInt(9);
//							if (expEquipType < 6) {
//								expEquipType = 6 + expEquipType / 2;
//							}
//							boolean issucc = robot.testFixEquip(1, 0, 1,
//									expEquipType);
//							System.out.println(i + "~~~~~~~" + issucc
//									+ "         " + expEquipType+ "    " +accountId);
//						}
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
		long avgTimeCost = timeCost.get() / finishCount.get();

		tmpLog.info("tasks all ongoing ; success:" + successCount.get()
				+ " fail:" + failCount.get() + " finishCount:"
				+ finishCount.get() + " avg in ms:" + avgTimeCost
				+ " maxTimeCost:" + maxTimeCost.get());
	}

	// 注册创建角色
	private static Robot createRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.regPlatform()) {
			long startTime = System.currentTimeMillis();

			if (robot.creatRole()) {
				if (withCarrer) {
					if (robot.upgrade(60) && robot.selectCarrer()) {
						successCount.incrementAndGet();
					} else {
						failCount.incrementAndGet();
					}

				} else {
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
		} else {
			failCount.incrementAndGet();
		}
		return robot;
	}

}
