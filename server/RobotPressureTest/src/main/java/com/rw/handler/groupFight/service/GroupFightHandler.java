package com.rw.handler.groupFight.service;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.dataSyn.JsonUtil;
import com.rw.handler.group.data.UserGroupData;
import com.rw.handler.groupFight.data.GFightDataVersion;
import com.rw.handler.groupFight.data.GFightOnlineGroupHolder;
import com.rw.handler.groupFight.dataForClient.DefendArmyHerosInfo;
import com.rw.handler.groupFight.dataForRank.GFBidRankHolder;
import com.rw.handler.groupFight.dataForRank.GFGroupBiddingItem;
import com.rw.handler.hero.TableUserHero;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.GrouFightOnlineProto.GFRequestType;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineReqMsg;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupFightHandler {

	private static GroupFightHandler handler = new GroupFightHandler();

	public static GroupFightHandler getHandler() {
		return handler;
	}
	
	public boolean playGroupFightBid(Client client) {
		boolean result = synGroupFight(client);
		if (!result) {
			RobotLog.fail("playGroupFightBid[send]在线帮战同步资源点信息反馈结果=" + result);
			return result;
		}
		result = getGroupBidRank(client);
		if (!result) {
			RobotLog.fail("playGroupFightBid[send]获取帮派竞标排名反馈结果=" + result);
			return result;
		}
		result = groupBid(client);
		if (!result) {
			RobotLog.fail("playGroupFightBid[send]帮派竞标反馈结果=" + result);
			return result;
		}
		RobotLog.info("playGroupFightBid[send]在线帮战竞标操作成功=" + result);
		return result;
	}
	
	public boolean playGroupFightPrepare(Client client) {
		boolean result = synGroupFight(client);
		if (!result) {
			RobotLog.fail("playGroupFightPrepare[send]在线帮战同步资源点信息反馈结果=" + result);
			return result;
		}
		List<String> rankList = GFightOnlineGroupHolder.getInstance().getRankIDList();
		if(null == rankList || rankList.isEmpty()){
			RobotLog.info("playGroupFightPrepare[send]在线帮战备战阶段,无帮派进入此阶段");
			return true;
		}
		UserGroupData userGroup = client.getUserGroupDataHolder().getUserGroupData();
		if(null == userGroup || !rankList.contains(userGroup.getGroupId())){
			//没有帮派，或者帮派没进前四，玩家只能压标
			
		}else{
			//帮派进前四，玩家可以备战
			modifySelfDefender(client);
		}
		RobotLog.info("playGroupFightPrepare[send]在线帮战备战操作成功=" + result);
		return result;
	}
	
	private boolean synGroupFight(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GET_RESOURCE_INFO);
		req.setResourceID(2);
		req.setClientVersion(JsonUtil.writeValue(new GFightDataVersion()));
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_FIGHT_ONLINE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_FIGHT_ONLINE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GroupFightOnlineRspMsg rsp = GroupFightOnlineRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupFightHandler[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupFightHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean getGroupBidRank(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GET_GROUP_BID_RANK);
		req.setResourceID(2);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_FIGHT_ONLINE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_FIGHT_ONLINE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GroupFightOnlineRspMsg rsp = GroupFightOnlineRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupFightHandler[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
					GFBidRankHolder.getInstance().updateBidRank(2, rsp.getRankDataList());
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupFightHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean groupBid(Client client){
		List<GFGroupBiddingItem> rankItems = GFBidRankHolder.getInstance().getBidRank(2);
		int count = 100;
		if(null != rankItems && !rankItems.isEmpty()) {
			int lastIndex = rankItems.size();
			if(lastIndex > 4) lastIndex = 4;
			count += rankItems.get(lastIndex -1).getTotalBidding(); 
		}
		final int bidCount = count;
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GROUP_BIDDING);
		req.setResourceID(2);
		req.setBidCount(bidCount);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_FIGHT_ONLINE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_FIGHT_ONLINE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GroupFightOnlineRspMsg rsp = GroupFightOnlineRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupFightHandler[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
					GFBidRankHolder.getInstance().updateBidRank(2, rsp.getRankDataList());
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupFightHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean personalBid(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GET_RESOURCE_INFO);
		req.setResourceID(2);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_FIGHT_ONLINE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_FIGHT_ONLINE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GroupFightOnlineRspMsg rsp = GroupFightOnlineRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupFightHandler[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupFightHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean modifySelfDefender(Client client){
		TableUserHero heros = client.getUserHerosDataHolder().getTableUserHero();
		if(heros == null || heros.getHeroIds().size() <= 1) return true;
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.MODIFY_SELF_DEFENDER);
		req.setResourceID(2);
		for(int i = 1; i <= 5; i++){
			List<String> heroIDs = new ArrayList<String>();
			for(int j = 4*(i-1) + 1; j < 4*i + 1 && j < heros.getHeroIds().size() - 1; j++){
				heroIDs.add(heros.getHeroIds().get(j));
			}
			DefendArmyHerosInfo defenderHeros = new DefendArmyHerosInfo();
			defenderHeros.setDefendArmyID(client.getUserId() + "_" + i);
			defenderHeros.setMagicID("");
			defenderHeros.setHeroIDs(heroIDs);
			req.addArmyHeros();
		}
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_FIGHT_ONLINE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_FIGHT_ONLINE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GroupFightOnlineRspMsg rsp = GroupFightOnlineRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("modifySelfDefender[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("modifySelfDefender[send] 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("modifySelfDefender[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
}