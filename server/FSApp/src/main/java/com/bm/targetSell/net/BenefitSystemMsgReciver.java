package com.bm.targetSell.net;

import java.io.BufferedReader;
import java.io.IOException;

import com.log.GameLog;
import com.rwbase.gameworld.GameWorldFactory;

public class BenefitSystemMsgReciver {

	private final BufferedReader reader;

	private volatile boolean stop = false;
	
	private BenefitSystemMsgService msgService;
	
	public BenefitSystemMsgReciver(BufferedReader reader) {
		this.reader = reader;
		msgService = BenefitSystemMsgService.getHandler();
		startReciver();
	}

	
	
	
	private void startReciver(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!stop) {
					try {
						
						final String reString = reader.readLine();
						
						GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
							
							@Override
							public void run() {
								msgService.doTask(reString);
							}
						});
						
					} catch (IOException e) {
						GameLog.error("TargetSell", "BenefitSystemMsgReciver[startReciver]", "读取精准营销消息异常", e);
					}
				}
			}
		}).start();
	}
	
	/**
	 * 设置消息接收器关闭
	 * @throws IOException 
	 */
	public void setStop() throws IOException{
		reader.close();
		stop = true;
	}
	
	
	
}
