package com.rw.common;

import com.log.PlatformLog;
import com.rw.account.Account;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;

public class GameUtil {

	/***发送消息的最大字节数**/
	private static int maxMsgSize=20000;
	private static int baseMsgSize=5000;
	public static boolean  checkMsgSize(Response response) 
	{

		if(response.getSerializedContent().size() >= baseMsgSize){
			String st="返回消息"+ response.getHeader().getCommand().toString()+ "长度大于"+(maxMsgSize/1000)+"K";
			if(response.getSerializedContent().size() >= maxMsgSize){
				PlatformLog.error(st);
				return false;
			}else
			{
				PlatformLog.debug(st);
			}
		
		}
		
		return true;
    }
	
}
