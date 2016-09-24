package com.bm.targetSell.net;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bm.targetSell.TargetSellManager;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwbase.gameworld.GameWorldFactory;


public class BenefitMsgController{

	private static BenefitMsgController controller = new BenefitMsgController();
	
	
	private ConcurrentLinkedQueue<String> msgQueue = new ConcurrentLinkedQueue<String>();


	private BenefitSystemMsgAdapter msgSender;
	
	private AtomicBoolean shutDown = new AtomicBoolean(false);
	
	private BenefitMsgController() {
	}



	public static BenefitMsgController getInstance(){
		return controller;
	}
	
	public void init(String removeIp, int port, int timeoutMillis,int priod){
		
		msgSender = new BenefitSystemMsgAdapter(removeIp, port, timeoutMillis);
		FSGameTimerMgr.getInstance().createSecondTaskSubmitInfo(new HeartBeatTask(), priod);
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
			
			@Override
			public void run() {
				msgSender.connect();
			}
		});
		scanQueue();
	}


	//启动消息消费者线程
	private void scanQueue(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (!shutDown.get()) {
					
					String element = msgQueue.poll();
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
		msgSender.shutdown();
	}


	//心跳任务
	private class HeartBeatTask implements IGameTimerTask{

		@Override
		public String getName() {
			return "BenefitMsgQueueController#HeartBeatTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal)
				throws Exception {
			
//			if(!msgSender.isAvaliable()){
//				//还没有连接成功，这个时候进行重新连接
//				msgSender.connect();
//				return null;
//			}
//			if(msgQueue.isEmpty()){
//				//发送心跳消息
//				msgSender.sendMsg(TargetSellManager.getInstance().HeartBeatContent);
//				return null;
//			}
//			
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
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
