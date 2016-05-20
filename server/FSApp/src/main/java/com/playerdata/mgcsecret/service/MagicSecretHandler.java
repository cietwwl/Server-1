package com.playerdata.mgcsecret.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.mgcsecret.MagicSecretMgr;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;

public class MagicSecretHandler {

	private static MagicSecretHandler instance;	

	private MagicSecretHandler() {
	}

	public static MagicSecretHandler getInstance() {
		if (instance == null) {
			instance = new MagicSecretHandler();
		}
		return instance;
	}

	public ByteString getMSRankData(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getMSRankData(msRsp);
		return msRsp.build().toByteString();
	}

	public ByteString enterMSFight(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msMgr.enterMSFight());
		return msRsp.build().toByteString();
	}

	public ByteString getMSSingleReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getSingleReward(msRsp);
		return msRsp.build().toByteString();
	}
	
	public ByteString getMSSweepReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getMSSweepReward(msRsp);
		return msRsp.build().toByteString();
	}

	public ByteString openRewardBox(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.openRewardBox(msRsp);
		return msRsp.build().toByteString();
	}

	public ByteString exchangeBuff(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msMgr.exchangeBuff());
		return msRsp.build().toByteString();
	}
}