package com.bm.worldBoss.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.WorldBossProtos.CommonReqMsg;
import com.rwproto.WorldBossProtos.CommonRspMsg;

public class WBHandler {
	
	private static WBHandler instance = new WBHandler();
	
	public static WBHandler getInstance(){
		return instance;
	}	

	public ByteString DoEnter(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		
		
		
		response.setIsSuccess(true);
		
				
		return response.build().toByteString();
	}

	public ByteString DoFightBegin(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		
		
		
		response.setIsSuccess(true);
		
				
		return response.build().toByteString();
	}

	


}
