package com.rw.mutiTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.rw.Robot;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8.Fun;

public class MutiTestGroupCompetition {
	
	private static List<String> accountList = Arrays.asList("071529340","0718483047","amy","jenifer","angelina","hclovehf","scarlett","wayne","christian","brad","hclovehf0007","0804702605","0804113374","hclovehf0014","finland","sweden","norway","m26","m27","denmark","belgium","0826959354","0826738580","0826346791","testunlock10","0830365823","0830903579","0830982570","0830275643","luxembourg","0830550456","0831703387","0831736990","nederland","hungary","0906261546","0908457734","0908268797","0908250747","0908660972","0908815215","0908717934","0908644814","0908515517","0908910836","0908369709","0908871319","0908581907","0908943253","0908941592","0908882243","0908692200","0908841316","0908421189","0908964771","0908233600","0908150835","0908842629","austria","09097878","0910279714","czechia","slovenia","ireland","switzerland","slovak","testguide108","nb3","0922440419","0923797785","0924991182","nb4","0926392080","0926929998","0926118983","korea","singapore","newzealand","australia","canada","usa","mexico","melbourne","maryland","auckland","malaysia","0926799097","mtest1","mtest2","0929758373","1005508353","1005685080","france","germany","italy","texas","1005789957","1005443950","1005642914","1006301647","iceland","greenland","helsinki","1008422856","oslo","wellington","1010181833","1011931859","1011606979","ontario","qatar","saudi","1013433729","1013464059","1013353914","101380051","1014174680","anne","0705412825","0705753456");
	private static Logger tmpLog = Logger.getLogger("tmpLog");

	// 执行线程数，并发数
	private static int threadCount = MutiTestAccount.threadCount;

	final private static int totalCount = MutiTestAccount.totalCount;

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
	private static volatile boolean shutdown = true;

	public static void main(String[] args) throws Exception {

		final List<Robot> robotList = loginRobots();
//		for (final Robot robot : robotList) {
//			executorService.submit(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						while (executeCount.get() < totalCount) {
//							Thread.sleep(span/10);
//							long start = System.currentTimeMillis();
//							boolean success = robot.testGroupCompetition();
//							tmpLog.info(Thread.currentThread().getName() + ", 争霸赛综合测试:" + success + ", userId:" + robot.getUserId());
//							long cost = System.currentTimeMillis() - start;
//							timeCost.addAndGet(cost);
//							if (success)
//								successCount.incrementAndGet();
//							else
//								failCount.incrementAndGet();
//							if (maxTimecost.get() < cost) {
//								maxTimecost.set(cost);
//							}
//							executeCount.incrementAndGet();
//						}
//						robot.close();
//					} catch (Throwable e) {
//						e.printStackTrace();
//					} finally {
//						finishCount.incrementAndGet();
//					}
//				}
//			});
//			Thread.sleep(1000l);
//		}
//		while (executeCount.get() < totalCount) {
//			Thread.sleep(span);
//		}
//		long avgTimeCost = timeCost.get() / executeCount.get();
//		tmpLog.info("tasks all ongoing ; success:" + successCount.get()
//				+ " fail:" + failCount.get() + " finishCount:"
//				+ finishCount.get() + " avg in ms:" + avgTimeCost
//				+ " maxTimecost:" + maxTimecost.get());
		List<Runnable> runnableList = new ArrayList<Runnable>(robotList.size());
		for (int i = 0; i < robotList.size(); i++) {
			final int index = i;
			runnableList.add(new Runnable() {

				final Robot robot = robotList.get(index);

				@Override
				public void run() {
					long start = System.currentTimeMillis();
					boolean success = robot.testGroupCompetition();
					tmpLog.info(Thread.currentThread().getName() + ", 争霸赛综合测试:" + success + ", userId:" + robot.getUserId());
					long cost = System.currentTimeMillis() - start;
					timeCost.addAndGet(cost);
					if (success)
						successCount.incrementAndGet();
					else
						failCount.incrementAndGet();
					if (maxTimecost.get() < cost) {
						maxTimecost.set(cost);
					}
					executeCount.incrementAndGet();
				}
			});
		}
		while(!shutdown) {
			Collections.shuffle(runnableList);
			List<Future<?>> futureList = new ArrayList<Future<?>>();
			for(Runnable r : runnableList) {
				futureList.add(executorService.submit(r));
			}
			while (futureList.size() > 0) {
				for (Iterator<Future<?>> itr = futureList.iterator(); itr.hasNext();) {
					if (itr.next().isDone()) {
						itr.remove();
					}
				}
			}
		}
		tmpLog.info("tasks all ongoing ; success:" + successCount.get()
		+ " fail:" + failCount.get() + " finishCount:"
		+ finishCount.get() + " avg in ms:" + (timeCost.get() / executeCount.get())
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
		//for (int i = start; i < start + threadCount * 3; i++) {
		List<Future<?>> futureList = new ArrayList<Future<?>>();
		int totalCount = 0;
		for (int i = 0, size = accountList.size(); i < size; i++) {
			final String accountId = accountList.get(i);
			if(accountId.startsWith("0")) {
				continue;
			}
			Future<?> future = loginService.submit(new Runnable() {
				@Override
				public void run() {
					try {
						
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
			totalCount++;
			futureList.add(future);
//			Thread.sleep(100l);
		}
//		while(finishLoginCount.get() < threadCount * 3){
//			Thread.sleep(span);
//		}
//		Thread.sleep(2000);
		while(futureList.size() > 0) {
			for(Iterator<Future<?>> itr = futureList.iterator(); itr.hasNext();) {
				if(itr.next().isDone()) {
					itr.remove();
				}
			}
		}
		loginService.shutdownNow();
		tmpLog.info("+++++++++++++++++++++++++++++++++ login success:"
				+ robotList.size() + " total:" + totalCount);
		return robotList;
	}

	//登录角色
	private static Robot loginRobot(String accountId) {
		Robot robot = Robot.newInstance(accountId);
		if (robot.loginPlatform()) {
			if (robot.loginGame()) {
//				robot.addCoin(10000000);
//				robot.addGold(10000000);
				return robot;
			}
		}
		robot.close();
		return null;
	}
}
