package com.bm.targetSell.net;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.bm.targetSell.TargetSellManager;
import com.common.RemoteMessageEnum;
import com.rw.fsutil.remote.RemoteMessageService;
import com.rw.fsutil.remote.RemoteMessageServiceFactory;
import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class BenefitMsgController {
	
	//用于计算包头的key
	public final static int MSG_KEY = 13542;

	private RemoteMessageService<String, String> server;
	
	
	FSMessageDecoder<String> decoder = new BenefitMsgDecoder();

	FSMessageEncoder<String> encoder = new BenefitMsgEncoder();

	FSMessageExecutor<String> executor = new BenefitMsgExcutor();
	
	private static BenefitMsgController controller = new BenefitMsgController();


	protected BenefitMsgController() {
	}

	public static BenefitMsgController getInstance() {
		return controller;
	}

	public void init(String removeIp, int port, int localPort, int timeoutMillis, int priod) {
		server = RemoteMessageServiceFactory.createService(RemoteMessageEnum.RMType_Benefit.getId(), removeIp,port, 2, 2, decoder, encoder, executor);
		FSGameTimerMgr.getInstance().submitSecondTask(new HeartBeatTask(priod), priod);
	}

	public void addMsg(String content) {
		System.err.println("add msg:" + content);
		server.sendMsg(content);

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
			FSGameTimerMgr.getInstance().submitSecondTask(this, interval);

			String heartBeatData = TargetSellManager.getInstance().getHeartBeatMsgData();
			// 发送心跳消息
			addMsg(heartBeatData);
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
