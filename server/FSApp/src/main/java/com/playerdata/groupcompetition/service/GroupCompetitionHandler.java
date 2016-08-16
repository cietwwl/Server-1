package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rwproto.GroupCompetitionProto.CommonReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;

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

	public ByteString getMSRankData(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.getMSRankData(player, msRsp);
		return msRsp.build().toByteString();
	}

	public ByteString enterMSFight(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.enterMSFight(player, msRsp, msgMSRequest.getDungeonId());
		return msRsp.build().toByteString();
	}

	public ByteString getMSSingleReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.getSingleReward(player, msRsp, msgMSRequest.getDungeonId(), msgMSRequest.getFinishState());
		return msRsp.build().toByteString();
	}
	
	public ByteString getMSSweepReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.getMSSweepReward(player, msRsp, msgMSRequest.getChapterId());
		return msRsp.build().toByteString();
	}

	public ByteString openRewardBox(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.openRewardBox(player, msRsp, msgMSRequest.getChapterId(), msgMSRequest.getRwdBox());
		return msRsp.build().toByteString();
	}

	public ByteString exchangeBuff(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msRsp.setRstType(msMgr.exchangeBuff(player, msgMSRequest.getChapterId(), msgMSRequest.getBuffId()));
		return msRsp.build().toByteString();
	}
	
	public ByteString changeMSArmy(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msRsp.setRstType(msMgr.changeMSArmy(player, msgMSRequest.getArmyInfo()));
		return msRsp.build().toByteString();
	}
	
	public ByteString getScoreReward(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.getScoreReward(player, msRsp, msgMSRequest.getScoreRewardID());
		return msRsp.build().toByteString();
	}
	
	public ByteString getSelfMSRank(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.getSelfMSRank(player, msRsp);
		return msRsp.build().toByteString();
	}
	
	public ByteString giveUpRewardBox(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msRsp.setRstType(msMgr.giveUpRewardBox(player, msgMSRequest.getChapterId()));
		return msRsp.build().toByteString();
	}

	public ByteString giveUpBuff(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msRsp.setRstType(msMgr.giveBuff(player, msgMSRequest.getChapterId()));
		return msRsp.build().toByteString();
	}

	public ByteString enterPrepareArea(Player player, CommonReqMsg request) {
		// TODO Auto-generated method stub
		return null;
	}

	public ByteString informPreparePosition(Player player, CommonReqMsg request) {
		// TODO Auto-generated method stub
		return null;
	}
}