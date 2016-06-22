package com.log.logToDataCenter.rocketmq;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.log.logToDataCenter.client.LogToDataCenterSender;
import com.log.logToDataCenter.config.LogConst;

public enum RocketMQConsumer {
	INSTANCE;
	
	final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("PushConsumer");
	final ConcurrentHashMap<Long, byte[]> successMsg = new ConcurrentHashMap<Long, byte[]>(50000);
	final byte[] mapValue = new byte[0];
	
	{
		consumer.setNamesrvAddr(LogConst.TARGET_MQ_ADDR_AND_PORT);
		consumer.setConsumerGroup("PushConsumer");
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setPullBatchSize(2 * LogConst.CONSUME_MESSAGE_BATCH_MAX_SIZE);
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeMessageBatchMaxSize(LogConst.CONSUME_MESSAGE_BATCH_MAX_SIZE);
		consumer.setMaxReconsumeTimes(LogConst.MAX_CONSUME_TIMES);
		consumer.setConsumeThreadMin(LogConst.MAX_CHANNEL_COUNT);
	}
	
	public void startConsumer() throws MQClientException{
		consumer.subscribe(LogConst.TOPIC, LogConst.SUBEXPRESSION);
		consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
            		ConsumeConcurrentlyContext Context) {
            	System.out.println((System.currentTimeMillis() - LogToDataCenterSender.startTime) + "list size:" + list.size());
            	System.out.println("set size:" + successMsg.size());
            	for(MessageExt msg : list){
            		if (successMsg.containsKey(Long.valueOf(msg.getKeys()))){
            			successMsg.remove(Long.valueOf(msg.getKeys()));
            			if(successMsg.size() == 0) System.out.println((System.currentTimeMillis() - LogToDataCenterSender.startTime) + ": Success!");
            		}else {
            			System.out.println("###########(" + msg.getKeys() + "): Retry!");
            			try {
            				if(msg.getReconsumeTimes() >= 5 && System.currentTimeMillis() - msg.getBornTimestamp() > LogConst.UNRESEND_MQ_MAX_PERIOD) {
								LogToDataCenterSender.writeLog(Long.valueOf(msg.getKeys()), new String(msg.getBody(), "UTF-8"));
								consumer.sendMessageBack(msg, LogConst.DEFAULT_DELAY_TIME_LEVEL);
							}
            				else consumer.sendMessageBack(msg, LogConst.DEFAULT_DELAY_TIME_LEVEL + 1);
            			}catch (Exception e) {
            				e.printStackTrace();
            				try {
								RocketMQProducer.INSTANCE.writeErrorLogToDB(Long.valueOf(msg.getKeys()), new String(msg.getBody(), "UTF-8"));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
            			}
            		}
            	}
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
			public void run() {  
				consumer.shutdown();
			}  
		}));
		consumer.start();
	}
	
	public void consumeMessageFromMQ(long key){
		successMsg.put(key, mapValue);
	}
}
