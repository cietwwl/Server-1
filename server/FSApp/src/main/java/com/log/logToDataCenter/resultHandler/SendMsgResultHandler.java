package com.log.logToDataCenter.resultHandler;

import java.io.UnsupportedEncodingException;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.log.logToDataCenter.client.LogToDataCenterSender;
import com.log.logToDataCenter.exception.QueryMQFailException;
import com.log.logToDataCenter.rocketmq.RocketMQProducer;

public class SendMsgResultHandler {
	public void handleResult(boolean isSuccess, long removedKey) {
		if(!isSuccess){
			System.out.println("SendMsg Fail:" + removedKey);
			MessageExt msgExt = RocketMQProducer.INSTANCE.viewMessageFromMQ(removedKey);
			if(msgExt != null){
				try {
					LogToDataCenterSender.writeLog(removedKey, new String(msgExt.getBody(), "UTF_8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				msgExt = RocketMQProducer.INSTANCE.viewMessageFromMQAllTime(removedKey);
				if(msgExt == null) {
					// TODO MQ中查找不到消息，有异常需要记录
					(new QueryMQFailException(removedKey + "消息发送失败，可是MQ中没有该条记录")).printStackTrace();
				}
			}
		}
	}
}