package com.rounter.client.sender.node;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rounter.client.sender.config.LogConst;

public class LogToDataCenterSender implements Runnable{
	private static ExecutorService exec = Executors.newFixedThreadPool(LogConst.MAX_THREAD_COUNT);
	private final LogStoreInfo logInfo;

	public static void writeLog(long key, String value, boolean failSendAgain){
		exec.execute(new LogToDataCenterSender(key, value, 0, failSendAgain));
	}
	
	public static void writeLog(LogStoreInfo logInfo){
		exec.execute(new LogToDataCenterSender(logInfo));
	}
	
	private LogToDataCenterSender(long key, String log, int failTimes, boolean failSendAgain){
		this.logInfo = new LogStoreInfo(key, log, failTimes, failSendAgain);
	}
	
	private LogToDataCenterSender(LogStoreInfo logInfo){
		this.logInfo = logInfo;
	}

	public void run() {
		try {
			ChannelNodeManager.INSTANCE.getProperChannelNode().sendMessage(logInfo);
		} catch (Exception e) {
			//e.printStackTrace();
			if(logInfo.isFailSendAgain()) {
				WriteMappedFile.getInstance().writeLog(logInfo);
			}
		}
	}
}