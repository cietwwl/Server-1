package com.bm.sameScene.service;

import com.bm.sameScene.data.SameSceneMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.SaloonServiceProto.CommonReqMsg;
import com.rwproto.SaloonServiceProto.CommonRspMsg;

public class SameSceneHandler {

	private static SameSceneHandler instance;	

	private SameSceneHandler() {
	}

	public static SameSceneHandler getInstance() {
		if (instance == null) {
			instance = new SameSceneHandler();
		}
		return instance;
	}

	public ByteString enterPrepareArea(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		SameSceneMgr.getInstance().enterPrepareArea(player, gcRsp, request.getPosition());
		return gcRsp.build().toByteString();
	}

	public ByteString informPreparePosition(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		SameSceneMgr.getInstance().informPreparePosition(player, gcRsp, request.getPosition());
		return gcRsp.build().toByteString();
	}

	public ByteString leavePrepareArea(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		SameSceneMgr.getInstance().leavePrepareArea(player, gcRsp);
		return gcRsp.build().toByteString();
	}

	public ByteString getPlayersBaseInfo(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		SameSceneMgr.getInstance().applyUsersBaseInfo(player, gcRsp, request.getPlayerIdListList());
		return gcRsp.build().toByteString();
	}

	public void inPrepareArea(Player player) {
		SameSceneMgr.getInstance().inPrepareArea(player);
	}
}