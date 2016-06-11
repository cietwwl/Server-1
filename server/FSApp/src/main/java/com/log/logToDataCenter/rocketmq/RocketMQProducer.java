package com.log.logToDataCenter.rocketmq;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.log.logToDataCenter.client.LogToDataCenterSender;
import com.log.logToDataCenter.config.LogConst;
import com.log.logToDataCenter.errorStore.LogStoreDao;
import com.log.logToDataCenter.errorStore.LogStoreInfo;
import com.log.logToDataCenter.exception.QueryMQFailException;

public enum RocketMQProducer
{
	INSTANCE;
	
	final DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
	final AtomicLong atoKeys = new AtomicLong();
    
	{
		producer.setNamesrvAddr(LogConst.TARGET_MQ_ADDR_AND_PORT);  
	    producer.setInstanceName("Producer");
	    producer.setVipChannelEnabled(false);
	}
	
	public void startProducer() throws MQClientException{
		producer.start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
			public void run() {  
				producer.shutdown();  
			}  
		}));
	}
	
	public void sendMsg(final String sendContent) {
		final long key = atoKeys.incrementAndGet();
		Message msg = new Message(LogConst.TOPIC, LogConst.SUBEXPRESSION, String.valueOf(key), sendContent.getBytes());
		msg.setDelayTimeLevel(LogConst.DEFAULT_DELAY_TIME_LEVEL);
		try {
			SendResult result = producer.send(msg);
			if(result.getSendStatus() == SendStatus.SEND_OK){
				LogToDataCenterSender.writeLog(key, sendContent);
				writeErrorLogToDB(key, sendContent);
			}else {
				writeErrorLogToDB(key, sendContent);
				System.out.println(sendContent + "发送到MQ失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeErrorLogToDB(key, sendContent);
		}
	}
	
	/**
	 * 查询一定时间内的message
	 * @param key
	 * @return
	 * @throws RemotingException
	 * @throws MQBrokerException
	 * @throws InterruptedException
	 * @throws MQClientException
	 * @throws QueryMQFailException
	 */
	public MessageExt viewMessageFromMQ(long key){
		long now = System.currentTimeMillis();
		long from = now - LogConst.VIEW_MQ_MAX_PERIOD;
		try {
			QueryResult qr = producer.queryMessage(LogConst.TOPIC, String.valueOf(key), 1, from, now);
			return qr.getMessageList().get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 无时间限制，查询mq
	 * @param key
	 * @return
	 * @throws RemotingException
	 * @throws MQBrokerException
	 * @throws InterruptedException
	 * @throws MQClientException
	 * @throws QueryMQFailException
	 */
	public MessageExt viewMessageFromMQAllTime(long key) {
		try {
			QueryResult qr = producer.queryMessage(LogConst.TOPIC, String.valueOf(key), 1, 0, System.currentTimeMillis());
			return qr.getMessageList().get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void writeErrorLogToDB(long key, String log){
		LogStoreInfo logInfo = new LogStoreInfo(String.valueOf(key), log);
		LogStoreDao.getInstance().update(logInfo);
	}
}
