package com.common;

import com.log.GameLog;
import com.log.LogModule;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;

public class GameUtil {

	/***发送消息的最大字节数**/
	private static int maxMsgSize=30000;
	private static int baseMsgSize=5000;
	public static boolean  checkMsgSize(Response response) 
	{

		if(response.getSerializedContent().size() >= baseMsgSize){
			String errorReason="返回消息"+ response.getHeader().getCommand().toString()+ "长度大于"+(maxMsgSize/1000)+"K";
			if(response.getSerializedContent().size() >= maxMsgSize){
				 GameLog.error(LogModule.COMMON.name(), " GameUtil[checkMsgSize]", errorReason, null);
			}
		
		}
		
		return true;
    }
	
	public static boolean  checkMsgSize(ResponseProtos.Response.Builder response,String userId) 
	{
		if(response.getSerializedContent().size() >= baseMsgSize){
			String errorReason= userId+"  "+response.getHeader().getCommand().toString()+"  发送消息   长度大于"+(maxMsgSize/1000)+"K";
			if(response.getSerializedContent().size() >= maxMsgSize){
			   GameLog.error(LogModule.COMMON.name(), userId, errorReason, null);
			}
		}
		return true;
    }
	
}
