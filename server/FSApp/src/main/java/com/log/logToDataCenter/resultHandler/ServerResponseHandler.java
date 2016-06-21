package com.log.logToDataCenter.resultHandler;

import java.io.UnsupportedEncodingException;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.log.logToDataCenter.client.LogToDataCenterSender;
import com.log.logToDataCenter.exception.QueryMQFailException;
import com.log.logToDataCenter.rocketmq.RocketMQConsumer;
import com.log.logToDataCenter.rocketmq.RocketMQProducer;

public class ServerResponseHandler {
	public void handleResponse(boolean state, long key){
		if(!state){
			System.out.println("ServerResponse Fail:" + key);
			MessageExt msgExt = RocketMQProducer.INSTANCE.viewMessageFromMQ(key);
			if(msgExt != null){
				try {
					LogToDataCenterSender.writeLog(key, new String(msgExt.getBody(), "UTF_8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				msgExt = RocketMQProducer.INSTANCE.viewMessageFromMQAllTime(key);
				if(msgExt == null) {
					// TODO MQ中查找不到消息，有异常需要记录
					(new QueryMQFailException(key + "服务端返回失败，可是MQ中没有该条记录")).printStackTrace();
				}
			}
		}
		else RocketMQConsumer.INSTANCE.consumeMessageFromMQ(key);
	}
}