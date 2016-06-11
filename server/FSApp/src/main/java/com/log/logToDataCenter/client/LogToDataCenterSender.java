package com.log.logToDataCenter.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.log.logToDataCenter.config.LogConst;
import com.log.logToDataCenter.rocketmq.RocketMQConsumer;
import com.log.logToDataCenter.rocketmq.RocketMQProducer;

public class LogToDataCenterSender implements Runnable{
	private static ExecutorService exec = Executors.newFixedThreadPool(LogConst.MAX_THREAD_COUNT);
	private static ExecutorService userES = Executors.newFixedThreadPool(LogConst.MAX_THREAD_COUNT);
	public static long startTime = 0;
	
	private final String log;
	private final long key;
	
	public static void main(String[] args) throws InterruptedException, MQClientException, RemotingException, MQBrokerException {
		
		RocketMQProducer.INSTANCE.startProducer();
		Thread.sleep(1);
		RocketMQConsumer.INSTANCE.startConsumer();
		startTime = System.currentTimeMillis();
		for(int i = 1; i < 10000; i++){
			final String msg = String.valueOf(i);
			//if(i%10 == 0) Thread.yield();
			userES.execute(new Runnable() {
				public void run() {
					RocketMQProducer.INSTANCE.sendMsg(msg);
				}
			});
		}
		System.out.println(System.currentTimeMillis() - startTime);
	}
	
	public static void writeLog(long key, String value){
		exec.execute(new LogToDataCenterSender(key, value));
	}
	
	public LogToDataCenterSender(long key, String log){
		this.log = log;
		this.key = key;
	}
	
	public void run() {
		try {
			ChannelNodeManager.INSTANCE.getProperChannelNode().sendMessage(key, log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}