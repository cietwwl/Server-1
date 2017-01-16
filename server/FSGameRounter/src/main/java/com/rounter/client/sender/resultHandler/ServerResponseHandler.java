package com.rounter.client.sender.resultHandler;

import com.rounter.client.sender.node.LogToDataCenterSender;

public class ServerResponseHandler {
	public void handleResponse(boolean state, LogStoreInfo logInfo){
		if(!state){
			if(logInfo.isFailSendAgain()){
				System.out.println("SendMsg Fail:" + logInfo.getLog_content());
				if(logInfo.getFailTimes() < 3){
					logInfo.addOnceFailTime();
					LogToDataCenterSender.writeLog(logInfo);
				}else{
					WriteMappedFile.getInstance().writeLog(logInfo);
				}
			}
		}else{
			//System.out.println("ServerResponseHandler-SendMsg Success:" + logInfo.getLog_content());
		}
	}
}