package com.playerdata.groupFightOnline.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.GroupChampProto.CommonReqMsg;
import com.rwproto.GroupChampProto.CommonRspMsg;


public class GFightOnlineHandler {
	
	private static GFightOnlineHandler instance = new GFightOnlineHandler();
	
	public static GFightOnlineHandler getInstance(){
		return instance;
	}



	public ByteString enter(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		
		
		return response.build().toByteString();
	}



}
