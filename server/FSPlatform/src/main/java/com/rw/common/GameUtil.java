package com.rw.common;

import com.log.GameLog;
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
				GameLog.error(st);
				return false;
			}else
			{
				GameLog.debug(st);
			}
		
		}
		
		return true;
    }
	
	public static boolean  checkMsgSize(ResponseProtos.Response.Builder response,Account account) 
	{
		if(response.getSerializedContent().size() >= baseMsgSize){
			String errorReason=account.getAccountId()+"  "+response.getHeader().getCommand().toString()+"  发送消息   长度大于"+(maxMsgSize/1000)+"K";
			if(response.getSerializedContent().size() >= maxMsgSize){
			   account.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips,"Player" + "|" +account.getAccountId() + "|" + response.getHeader().getCommand().toString() + "|" + "发送消息" + "|" + "长度大于"+(maxMsgSize/1000)+"K" + "  " + response.getSerializedContent().size());
			  return false;
			}else
			{
				GameLog.debug(errorReason);
			}
		}
		return true;
    }
	
}
