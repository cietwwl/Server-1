package com.bm.targetSell.net;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bm.targetSell.TargetSellManager;
import com.log.GameLog;
import com.rw.fsutil.dao.cache.SimpleCache;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;


public class BenefitMsgController{

	private static BenefitMsgController controller = new BenefitMsgController();
	
	//等待发送的消息容器,如果消费者的效率低于生成效率，此容器会一直增长,后面要进行优化
	private ConcurrentLinkedQueue<String> msgQueue = new ConcurrentLinkedQueue<String>();

	private BenefitSystemMsgAdapter msgSender;
	
	private AtomicBoolean shutDown = new AtomicBoolean(false);
	
	
	private BenefitMsgController() {
	}



	public static BenefitMsgController getInstance(){
		return controller;
	}
	
	public void init(String removeIp, int port, int localPort, int timeoutMillis,int priod){
		msgSender = new BenefitSystemMsgAdapter(removeIp, port, localPort, timeoutMillis);
		
		FSGameTimerMgr.getInstance().submitSecondTask(new HeartBeatTask(priod), priod);
		
		scanQueue();
	}


	//启动消息消费者线程
	private void scanQueue(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (!shutDown.get()) {
					
					String element = msgQueue.poll();//
					if(element == null){
						continue;
					}
					if(msgSender.isAvaliable()){
						msgSender.sendMsg(element);
					}
				}
			
				
			}
		}).start();
	}
	
	public void shutDownNotify(){
		shutDown.compareAndSet(false, true);
		if (msgSender != null) {
			msgSender.shutdown();
		}
	}

	
	
	public void addMsg(String content) {
		msgQueue.add(content);
	}
	
	

	//心跳任务
	private class HeartBeatTask implements IGameTimerTask{

		private int interval;
		
		
		
		public HeartBeatTask(int interval) {
			this.interval = interval;
		}

		@Override
		public String getName() {
			return "BenefitMsgQueueController#HeartBeatTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal)
				throws Exception {
			if(shutDown.get()){
				return null;
			}
			int size = msgQueue.size();
			GameLog.info("TargetSell", "watch task", "current wait for sending msg count:" + size);
			FSGameTimerMgr.getInstance().submitSecondTask(this, interval);
			if(!msgSender.isAvaliable()){
				//还没有连接成功，这个时候进行重新连接
				msgSender.connect();
				return null;
			}
			if(msgQueue.isEmpty()){
				String heartBeatData = TargetSellManager.getInstance().getHeartBeatMsgData();
				//发送心跳消息
				msgSender.sendMsg(heartBeatData);
				return null;
			}
			
			
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
