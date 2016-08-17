package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.GroupCompetitionProto.CommonReqMsg;
import com.rwproto.GroupCompetitionProto.CommonRspMsg;

public class GroupCompetitionHandler {

	private static GroupCompetitionHandler instance;	

	private GroupCompetitionHandler() {
	}

	public static GroupCompetitionHandler getInstance() {
		if (instance == null) {
			instance = new GroupCompetitionHandler();
		}
		return instance;
	}

	public ByteString enterPrepareArea(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		
		return gcRsp.build().toByteString();
	}

	public ByteString informPreparePosition(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		
		return gcRsp.build().toByteString();
	}

	public ByteString leavePrepareArea(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		
		return gcRsp.build().toByteString();
	}
}