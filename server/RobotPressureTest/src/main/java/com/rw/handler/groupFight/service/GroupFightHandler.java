package com.rw.handler.groupFight.service;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.battle.army.CurAttrData;
import com.rw.handler.group.data.UserGroupData;
import com.rw.handler.groupFight.data.GFDefendArmyItem;
import com.rw.handler.groupFight.data.GFDefendArmyItemHolder;
import com.rw.handler.groupFight.data.GFightOnlineGroupData;
import com.rw.handler.groupFight.data.GFightOnlineGroupHolder;
import com.rw.handler.groupFight.data.GFightOnlineResourceData;
import com.rw.handler.groupFight.data.GFightOnlineResourceHolder;
import com.rw.handler.groupFight.data.UserGFightOnlineData;
import com.rw.handler.groupFight.data.UserGFightOnlineHolder;
import com.rw.handler.groupFight.dataForClient.DefendArmyHerosInfo;
import com.rw.handler.groupFight.dataForClient.GFightResult;
import com.rw.handler.groupFight.dataForRank.GFBidRankHolder;
import com.rw.handler.groupFight.dataForRank.GFGroupBiddingItem;
import com.rw.handler.hero.TableUserHero;
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
	
	public static int RESOURCE_ID = 2;
	
	public boolean playGroupFight(Client client){
		boolean result = synGroupFight(client);
		if (!result) {
			RobotLog.fail("playGroupFight[send]在线帮战同步资源点信息反馈结果=" + result);
			return result;
		}
		GFightOnlineResourceData gfResData = GFightOnlineResourceHolder.getInstance().getUserGFData(RESOURCE_ID);
		switch(gfResData.getState()){
		case 1://休战
			return true;
		case 2://竞标阶段
			return playGroupFightBid(client);
		case 3://备战阶段
			return playGroupFightPrepare(client);
		case 4://开战阶段
			return playGFStartFight(client);
		default:
			return true;
		}
	}
	
	
	/**
	 * 帮战竞标阶段
	 * @param client
	 * @return
	 */
	private boolean playGroupFightBid(Client client) {
		boolean result = getGroupBidRank(client);
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
	
	/**
	 * 帮战备战阶段
	 * @param client
	 * @return
	 */
	private boolean playGroupFightPrepare(Client client) {
		boolean result = true;
		List<String> rankList = GFightOnlineGroupHolder.getInstance().getRankIDList();
		if(null == rankList || rankList.isEmpty()){
			RobotLog.info("playGroupFightPrepare[send]在线帮战备战阶段,无帮派进入此阶段");
			return true;
		}
		UserGroupData userGroup = client.getUserGroupDataHolder().getUserGroupData();
		if(null == userGroup || !rankList.contains(userGroup.getGroupId())){
			//没有帮派，或者帮派没进前四，玩家只能压标
			result = personalBid(client, rankList);
			if (!result) {
				RobotLog.fail("playGroupFightPrepare[send]在线帮战备战压标反馈结果=" + result);
				return result;
			}
		}else{
			//帮派进前四，玩家可以备战
			result = modifySelfDefender(client);
			if (!result) {
				RobotLog.fail("playGroupFightPrepare[send]在线帮战备战布阵反馈结果=" + result);
				return result;
			}
		}
		RobotLog.info("playGroupFightPrepare[send]在线帮战备战操作成功=" + result);
		return result;
	}
	
	/**
	 * 开战阶段
	 * @param client
	 * @return
	 */
	private boolean playGFStartFight(Client client) {
		boolean result = true;
		List<String> rankList = GFightOnlineGroupHolder.getInstance().getRankIDList();
		if(null == rankList || rankList.isEmpty()){
			RobotLog.info("playGFStartFight[send]在线帮战开战阶段,无帮派进入此阶段");
			return true;
		}
		UserGroupData userGroup = client.getUserGroupDataHolder().getUserGroupData();
		if(null == userGroup || !rankList.contains(userGroup.getGroupId())){
			RobotLog.info("playGFStartFight[send]在线帮战开战阶段,所在帮派没进前四");
			return true;
		}else{
			List<String> enimyList = new ArrayList<String>();
			for(String groupID : rankList){
				if(!groupID.equals(userGroup.getGroupId())){
					GFightOnlineGroupData otherGroupData = GFightOnlineGroupHolder.getInstance().getUserGFData(groupID);
					if(otherGroupData.getAliveCount() > 0) enimyList.add(groupID); 
				}
			}
			if(enimyList.isEmpty()){
				RobotLog.info("playGFStartFight[send]在线帮战开战阶段,已经没有可以挑战的对手");
				return true;
			}
			int rankIndex = (int)(Math.random() * enimyList.size());
			if(getEnimyDefender(client, enimyList.get(rankIndex))){
				UserGFightOnlineData ugfData = UserGFightOnlineHolder.getInstance().getUserGFData(client.getUserId());
				if(null != ugfData.getRandomDefender() && System.currentTimeMillis() - ugfData.getRandomDefender().getLockArmyTime() < 2*60*1000){
					//锁定的对手在有效期内
					changeEnimyDefender(client, ugfData.getRandomDefender().getGroupID());
					startFight(client);
					informFightResult(client);
				}
			}
		}
		RobotLog.info("playGFStartFight[send]在线帮战开战操作成功=" + result);
		return result;
	}
	
	/**
	 * 同步帮战数据
	 * @param client
	 * @return
	 */
	private boolean synGroupFight(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GET_RESOURCE_INFO);
		req.setResourceID(RESOURCE_ID);
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
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + rsp.getTipMsg());
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
	
	/**
	 * 获取竞标排行
	 * @param client
	 * @return
	 */
	private boolean getGroupBidRank(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GET_GROUP_BID_RANK);
		req.setResourceID(RESOURCE_ID);
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
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + rsp.getTipMsg());
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
	
	/**
	 * 帮派竞标
	 * @param client
	 * @return
	 */
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
		req.setResourceID(RESOURCE_ID);
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
						RobotLog.fail("GroupFightHandler[send] 服务器处理消息失败 " + rsp.getTipMsg());
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

	/**
	 * 个人压标
	 * @param client
	 * @param rankList
	 * @return
	 */
	private boolean personalBid(Client client, List<String> rankList){
		int index = (int)(Math.random() * rankList.size());
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.PERSONAL_BIDDING);
		req.setResourceID(RESOURCE_ID);
		req.setGroupID(rankList.get(index));
		req.setSelfBidRate(2);
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
						RobotLog.fail("personalBid[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("personalBid[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("personalBid[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	/**
	 * 修改个人的队伍
	 * @param client
	 * @return
	 */
	private boolean modifySelfDefender(Client client){
		TableUserHero heros = client.getUserHerosDataHolder().getTableUserHero();
		if(heros == null || heros.getHeroIds().size() <= 1) return true;
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.MODIFY_SELF_DEFENDER);
		req.setResourceID(RESOURCE_ID);
		for(int i = 1; i <= 5; i++){
			List<String> heroIDs = new ArrayList<String>();
			for(int j = 4*(i-1) + 1; j < 4*i + 1 && j < heros.getHeroIds().size() - 1; j++){
				int rdm = (int)(Math.random() * 4);
				if(0 == rdm) heroIDs.add(heros.getHeroIds().get(j));
			}
			DefendArmyHerosInfo defenderHeros = new DefendArmyHerosInfo();
			defenderHeros.setDefendArmyID(client.getUserId() + "_" + i);
			defenderHeros.setMagicID("");
			defenderHeros.setHeroIDs(heroIDs);
			req.addArmyHeros(ClientDataSynMgr.toClientData(defenderHeros));
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
						RobotLog.fail("modifySelfDefender[send] 服务器处理消息失败 " + rsp.getTipMsg());
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
	
	/**
	 * 选择一个对手
	 * @param client
	 * @param enimyGroupID
	 * @return
	 */
	private boolean getEnimyDefender(Client client, String enimyGroupID){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.GET_ENIMY_DEFENDER);
		req.setResourceID(RESOURCE_ID);
		req.setGroupID(enimyGroupID);
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
						RobotLog.fail("getEnimyDefender[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("getEnimyDefender[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return false;
					}
					try {
						GFDefendArmyItemHolder.getInstance().updateSelectedEnimy((GFDefendArmyItem)ClientDataSynMgr.fromClientJson2Data(GFDefendArmyItem.class, rsp.getEnimyDefenderDetails()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("getEnimyDefender[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	/**
	 * 切换对手
	 * @param client
	 * @param enimyGroupID
	 * @return
	 */
	private boolean changeEnimyDefender(Client client, String enimyGroupID){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.CHANGE_ENIMY_DEFENDER);
		req.setResourceID(RESOURCE_ID);
		req.setGroupID(enimyGroupID);
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
						RobotLog.fail("getEnimyDefender[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("getEnimyDefender[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return false;
					}
					try {
						GFDefendArmyItemHolder.getInstance().updateSelectedEnimy((GFDefendArmyItem)ClientDataSynMgr.fromClientJson2Data(GFDefendArmyItem.class, rsp.getEnimyDefenderDetails()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("getEnimyDefender[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	/**
	 * 开始战斗
	 * @param client
	 * @return
	 */
	private boolean startFight(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.START_FIGHT);
		req.setResourceID(RESOURCE_ID);
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
						RobotLog.fail("startFight[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("startFight[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("startFight[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	/**
	 * 通知战斗结果
	 * @param client
	 * @return
	 */
	private boolean informFightResult(Client client){
		GroupFightOnlineReqMsg.Builder req = GroupFightOnlineReqMsg.newBuilder();
		req.setReqType(GFRequestType.INFORM_FIGHT_RESULT);
		req.setResourceID(RESOURCE_ID);
		List<CurAttrData> state = new ArrayList<CurAttrData>();
		int heroCount = GFDefendArmyItemHolder.getInstance().getSelfEnimy().getSimpleArmy().getHeroList().size() + 1;
		for(int i = 0; i < heroCount; i++){
			CurAttrData attrData = new CurAttrData();
			attrData.setId("0");
			state.add(attrData);
		}
		UserGFightOnlineData ugfData = UserGFightOnlineHolder.getInstance().getUserGFData(client.getUserId());
		GFightResult gfResult = new GFightResult();
		gfResult.setState(1);
		gfResult.setHurtValue((int)(Math.random() * 10000));
		gfResult.setDefendArmyID(ugfData.getRandomDefender().getDefendArmyID());
		gfResult.setGroupID(ugfData.getRandomDefender().getGroupID());
		gfResult.setDefenderState(state);
		gfResult.setSelfArmyState(state);
		req.setFightResult(ClientDataSynMgr.toClientData(gfResult));
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
						RobotLog.fail("informFightResult[send] 转换响应消息为null");
						return false;
					}
					GFResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.fail("informFightResult[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("informFightResult[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
}