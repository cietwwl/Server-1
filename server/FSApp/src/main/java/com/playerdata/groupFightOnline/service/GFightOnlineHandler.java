package com.playerdata.groupFightOnline.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.manager.GFightGroupBidMgr;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineReqMsg;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

public class GFightOnlineHandler {
	
	private static GFightOnlineHandler instance = new GFightOnlineHandler();
	
	public static GFightOnlineHandler getInstance(){
		return instance;
	}
	
	public ByteString getResourceInfo(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightGroupBidMgr.getInstance().getResourceInfo(player, gfRsp);
		return gfRsp.build().toByteString();
	}
	
	public ByteString groupBidding(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightGroupBidMgr.getInstance().groupBidding(player, gfRsp, msgGFRequest.getResourceID(), msgGFRequest.getBidCount());
		return gfRsp.build().toByteString();
	}
	
	public ByteString personalBidding(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString modifySelfDefender(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getEnimyDefender(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString changeEnimyDefender(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString startFight(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString informFightResult(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getGroupBidRank(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightGroupBidMgr.getInstance().getGroupBidRank(player, gfRsp, msgGFRequest.getResourceID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getKillRank(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getHurtRank(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getAllRankInGroup(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getDefenderTeams(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString viewDefenderTeam(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getFightRecord(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getFightOverReward(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		return gfRsp.build().toByteString();
	}
}
