package com.playerdata.mgcsecret.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
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
		msMgr.getMSRankData(player, msRsp);
		return msRsp.build().toByteString();
	}

	public ByteString enterMSFight(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.enterMSFight(player, msRsp, msgMSRequest.getDungeonId());
		return msRsp.build().toByteString();
	}

	public ByteString getMSSingleReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getSingleReward(player, msRsp, msgMSRequest.getDungeonId(), msgMSRequest.getFinishState());
		return msRsp.build().toByteString();
	}
	
	public ByteString getMSSweepReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getMSSweepReward(player, msRsp, msgMSRequest.getChapterId());
		return msRsp.build().toByteString();
	}

	public ByteString openRewardBox(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.openRewardBox(player, msRsp, msgMSRequest.getChapterId(), msgMSRequest.getRwdBox());
		return msRsp.build().toByteString();
	}

	public ByteString exchangeBuff(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msMgr.exchangeBuff(player, msgMSRequest.getChapterId(), msgMSRequest.getBuffId()));
		return msRsp.build().toByteString();
	}
	
	public ByteString changeMSArmy(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msMgr.changeMSArmy(player, msgMSRequest.getArmyInfo()));
		return msRsp.build().toByteString();
	}
	
	public ByteString getScoreReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getScoreReward(player, msRsp, msgMSRequest.getScoreRewardID());
		return msRsp.build().toByteString();
	}
	
	public ByteString getSelfMSRank(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msMgr.getSelfMSRank(player, msRsp);
		return msRsp.build().toByteString();
	}
	
	public ByteString giveUpRewardBox(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msMgr.giveUpRewardBox(player, msgMSRequest.getChapterId()));
		return msRsp.build().toByteString();
	}

	public ByteString giveUpBuff(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		msRsp.setRstType(msMgr.giveBuff(player, msgMSRequest.getChapterId()));
		return msRsp.build().toByteString();
	}
}