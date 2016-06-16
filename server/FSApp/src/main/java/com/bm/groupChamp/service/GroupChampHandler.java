package com.bm.groupChamp.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.GroupChampProto.CommonReqMsg;
import com.rwproto.GroupChampProto.CommonRspMsg;


public class GroupChampHandler {
	
	private static GroupChampHandler instance = new GroupChampHandler();
	
	public static GroupChampHandler getInstance(){
		return instance;
	}



	public ByteString enter(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		
		
		return response.build().toByteString();
	}



}
