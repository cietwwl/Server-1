package com.playerdata.mgcsecret.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.mgcsecret.MagicSecretMgr;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msResultType;

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
		String userId = player.getUserId();

		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());

		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msResultType.SUCCESS);
		return msRsp.build().toByteString();
	}

	public ByteString enterMSFight(Player player, MagicSecretReqMsg msgMSRequest) {
		String userId = player.getUserId();

		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());

		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.enterMSFight();
		msRsp.setRstType(msResultType.SUCCESS);
		return msRsp.build().toByteString();
	}

	public ByteString getMSSingleReward(Player player, MagicSecretReqMsg msgMSRequest) {
		String userId = player.getUserId();

		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());

		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msResultType.SUCCESS);
		return msRsp.build().toByteString();
	}

	public ByteString openRewardBox(Player player, MagicSecretReqMsg msgMSRequest) {
		String userId = player.getUserId();

		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());

		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msResultType.SUCCESS);
		return msRsp.build().toByteString();
	}

	public ByteString exchangeBuff(Player player, MagicSecretReqMsg msgMSRequest) {
		String userId = player.getUserId();

		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());

		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msResultType.SUCCESS);
		return msRsp.build().toByteString();
	}
}