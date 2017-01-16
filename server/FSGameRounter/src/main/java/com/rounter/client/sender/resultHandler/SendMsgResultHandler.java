package com.rounter.client.sender.resultHandler;

import com.rounter.client.sender.node.LogToDataCenterSender;

public class SendMsgResultHandler {
	public void handleResult(boolean isSuccess, LogStoreInfo logAfterSend) {
		if(!isSuccess){
			System.out.println("SendMsg Fail:" + logAfterSend.getLog_content());
			if(logAfterSend.isFailSendAgain()){
				if(logAfterSend.getFailTimes() < 3){
					logAfterSend.addOnceFailTime();
					LogToDataCenterSender.writeLog(logAfterSend);
				}else{
					WriteMappedFile.getInstance().writeLog(logAfterSend);
				}
			}
		}else{
			//System.out.println("SendMsgResultHandler-SendMsg Success:" + logAfterSend.getLog_content());
		}
	}
}