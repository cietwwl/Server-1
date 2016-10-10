package com.bm.targetSell.net;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bm.targetSell.TargetSellManager;
import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class BenefitMsgController {

	private static BenefitMsgController controller = new BenefitMsgController();

	private ThreadPoolExecutor excutor;

	private BenefitSystemMsgAdapter msgAdapter;

	private AtomicBoolean shutDown = new AtomicBoolean(false);

	private BenefitMsgController() {
	}

	public static BenefitMsgController getInstance() {
		return controller;
	}

	public void init(String removeIp, int port, int timeoutMillis, int priod) {

		excutor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(300));
		// 设置饱和策略,达到上限放弃最旧的
		excutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

		msgAdapter = new BenefitSystemMsgAdapter(removeIp, port, timeoutMillis);

		FSGameTimerMgr.getInstance().submitSecondTask(new HeartBeatTask(priod), priod);

		ShutdownService.registerShutdownService(new IShutdownHandler() {

			@Override
			public void notifyShutdown() {
				shutDownNotify();
			}
		});
	}

	private void shutDownNotify() {
		shutDown.compareAndSet(false, true);

		if (excutor != null) {
			excutor.shutdown();
			try {
				excutor.awaitTermination(30L, TimeUnit.SECONDS);
			} catch (InterruptedException e) {

			} finally {
				// 无论最后出现什么异常还是要关闭socket
				msgAdapter.shutdown();
			}
		}
	}

	public void addMsg(final String content) {

		excutor.execute(new Runnable() {

			@Override
			public void run() {
				if (msgAdapter.isAvaliable()) {
					msgAdapter.sendMsg(content);
				}

			}
		});

	}

	// 心跳任务
	private class HeartBeatTask implements IGameTimerTask {

		private int interval;

		public HeartBeatTask(int interval) {
			this.interval = interval;
		}

		@Override
		public String getName() {
			return "BenefitMsgQueueController#HeartBeatTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			if (shutDown.get()) {
				return null;
			}
			FSGameTimerMgr.getInstance().submitSecondTask(this, interval);
			if (!msgAdapter.isAvaliable()) {
				// 还没有连接成功，这个时候进行重新连接
				msgAdapter.connect();
				return null;
			}

			String heartBeatData = TargetSellManager.getInstance().getHeartBeatMsgData();
			// 发送心跳消息
			msgAdapter.sendMsg(heartBeatData);
			return null;

		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {

		}

		@Override
		public void rejected(RejectedExecutionException e) {

		}

		@Override
		public boolean isContinue() {
			return false;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}

	}

}
