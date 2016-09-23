package com.playerdata.groupcompetition.service;

import org.springframework.util.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleEndReqMsg;
import com.rwproto.GroupCompetitionBattleProto.GCUploadHpInfoReqMsg;

/**
 * @Author HC
 * @date 2016年9月22日 下午4:28:16
 * @desc 帮派争霸战的战斗处理
 **/

public class GroupCompetitionBattleHandler {
	private static GroupCompetitionBattleHandler handler = new GroupCompetitionBattleHandler();

	public static GroupCompetitionBattleHandler getHandler() {
		return handler;
	}

	GroupCompetitionBattleHandler() {
	}

	/**
	 * 帮派战开始的处理
	 * 
	 * @param player
	 * @return
	 */
	public ByteString battleStartHandler(Player player) {
		String userId = player.getUserId();

		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {
			return fillFailMsg(rsp, "您没有帮派");
		}

		GCEventsType currentEventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsDataMgr matchDataMgr = GCompEventsDataMgr.getInstance();
		int matchId = matchDataMgr.getMatchIdOfGroup(groupId, currentEventsType);
		if (matchId <= 0) {
			return fillFailMsg(rsp, "您当前没有匹配到对手");
		}

		// IGCAgainst matchAgainst = matchDataMgr.getMatchAgainstByMatchId(matchId, currentEventsType);
		// if (matchAgainst == null) {
		// return fillFailMsg(rsp, "您当前没有匹配到对手");
		// }
		//
		// IGCGroup winGroup = matchAgainst.getWinGroup();
		// if (winGroup != null) {
		// return fillFailMsg(rsp, "该场战斗已经结束");
		// }
		//
		// IGCGroup groupA = matchAgainst.getGroupA();
		// IGCGroup groupB = matchAgainst.getGroupB();
		//
		// List<IGCUnit> aGroupUnits = groupA.getAllUnits();
		// List<IGCUnit> bGroupUnits = groupB.getAllUnits();
		//
		// boolean isAGroup = groupId.equals(groupA.getGroupId());
		// // 获取自己所属的队伍，找到自己的索引
		// int mineIndex = 0;
		//
		// List<IGCUnit> checkGroupUnits = isAGroup ? aGroupUnits : bGroupUnits;
		// for (int i = 0, size = checkGroupUnits.size(); i < size; i++) {
		// IGCUnit unit = checkGroupUnits.get(i);
		// if (unit.getId().equals(userId)) {
		// mineIndex = i;
		// break;
		// }
		// }
		//
		// // 把敌人信息找出来
		// String enemyUserId = (isAGroup ? bGroupUnits : aGroupUnits).get(mineIndex).getId();
		// GCTeamDataHolder teamDataHolder = GCTeamDataHolder.getInstance();
		// // 先获取到我自己的上阵阵容
		// ArmyInfoSimple mineArmyInfoSimple = teamDataHolder.getArmyInfoSimple(matchId, userId);
		// if (mineArmyInfoSimple == null) {
		// return fillFailMsg(rsp, "获取己方阵容信息失败");
		// }
		//
		// // 获取到敌人的阵容
		// ArmyInfoSimple enemyArmyInfoSimple = teamDataHolder.getArmyInfoSimple(matchId, enemyUserId);
		// if (enemyArmyInfoSimple == null) {
		// return fillFailMsg(rsp, "获取敌方阵容信息失败");
		// }
		//
		// String mineArmyInfoJson;
		// String enemyArmyInfoJson;
		// try {
		// mineArmyInfoJson = ArmyInfoHelper.getArmyInfo(mineArmyInfoSimple, true).toJson();
		// enemyArmyInfoJson = ArmyInfoHelper.getArmyInfo(enemyArmyInfoSimple, true).toJson();
		// } catch (Exception e) {
		// GameLog.error("帮派争霸开始战斗", userId, "转换敌我双方的ArmyInfoSimple到json出现异常", e);
		// return fillFailMsg(rsp, "获取敌方阵容信息失败");
		// }
		//
		// GCBattleStartRspMsg.Builder battleStartRsp = GCBattleStartRspMsg.newBuilder();
		// battleStartRsp.setMineArmyInfo(mineArmyInfoJson);
		// battleStartRsp.setEnemyArmyInfo(enemyArmyInfoJson);
		// rsp.setBattleStartRsp(battleStartRsp);

		return rsp.setIsSuccess(true).build().toByteString();
	}

	/**
	 * 上传英雄血量信息
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString uploadHpInfoHandler(Player player, GCUploadHpInfoReqMsg.Builder req) {
		String userId = player.getUserId();

		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {// 没有帮派
			return fillFailMsg(rsp, "");
		}

		GCEventsType currentEventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsDataMgr matchDataMgr = GCompEventsDataMgr.getInstance();
		int matchId = matchDataMgr.getMatchIdOfGroup(groupId, currentEventsType);
		if (matchId <= 0) {// 匹配的Id
			return fillFailMsg(rsp, "");
		}
		//
		// IGCAgainst matchAgainst = matchDataMgr.getMatchAgainstByMatchId(matchId, currentEventsType);
		// if (matchAgainst == null) {// 匹配数据
		// return fillFailMsg(rsp, "");
		// }
		//
		// IGCGroup winGroup = matchAgainst.getWinGroup();
		// if (winGroup != null) {// 是否已经产生了胜利的帮派
		// return fillFailMsg(rsp, "");
		// }
		//
		// IGCGroup groupA = matchAgainst.getGroupA();
		// IGCGroup groupB = matchAgainst.getGroupB();
		//
		// List<IGCUnit> aGroupUnits = groupA.getAllUnits();
		// List<IGCUnit> bGroupUnits = groupB.getAllUnits();
		//
		// boolean isAGroup = groupId.equals(groupA.getGroupId());
		// // 获取自己所属的队伍，找到自己的索引
		// int mineIndex = 0;
		//
		// List<String> playerIdList = new ArrayList<String>();// 要同步给的PlayerIdList
		//
		// List<IGCUnit> checkGroupUnits = isAGroup ? aGroupUnits : bGroupUnits;
		// for (int i = 0, size = checkGroupUnits.size(); i < size; i++) {
		// IGCUnit unit = checkGroupUnits.get(i);
		// if (unit.getId().equals(userId)) {
		// mineIndex = i;
		// continue;
		// }
		//
		// playerIdList.add(unit.getId());
		// }
		//
		// // 如果有人
		// if (!playerIdList.isEmpty()) {
		// GCPushHpInfoRspMsg.Builder pushRsp = GCPushHpInfoRspMsg.newBuilder();
		// pushRsp.setIndex(mineIndex);
		// pushRsp.setMineHpPercent(req.getMineHpPercent());
		// pushRsp.setEnemyHpPercent(req.getEnemyHpPercent());
		// ByteString pushByteString = pushRsp.build().toByteString();
		//
		// PlayerMgr playerMgr = PlayerMgr.getInstance();
		// for (int i = 0, size = playerIdList.size(); i < size; i++) {
		// Player p = playerMgr.find(playerIdList.get(i));
		// if (p == null) {
		// continue;
		// }
		//
		// p.SendMsg(Command.MSG_ACTIVITY_COUNTTYPE, pushByteString);
		// }
		// }

		return rsp.setIsSuccess(true).build().toByteString();
	}

	/**
	 * 战斗结束
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString battleEndHandler(Player player, GCBattleEndReqMsg req) {
		String userId = player.getUserId();

		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {
			return fillFailMsg(rsp, "您没有帮派");
		}

		GCEventsType currentEventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsDataMgr matchDataMgr = GCompEventsDataMgr.getInstance();
		int matchId = matchDataMgr.getMatchIdOfGroup(groupId, currentEventsType);
		if (matchId <= 0) {
			return fillFailMsg(rsp, "您当前没有匹配到对手");
		}

		// IGCAgainst matchAgainst = matchDataMgr.getMatchAgainstByMatchId(matchId, currentEventsType);
		// if (matchAgainst == null) {
		// return fillFailMsg(rsp, "您当前没有匹配到对手");
		// }
		//
		// IGCGroup winGroup = matchAgainst.getWinGroup();
		// if (winGroup != null) {
		// return fillFailMsg(rsp, "该场战斗已经结束");
		// }
		//
		// IGCGroup groupA = matchAgainst.getGroupA();
		// IGCGroup groupB = matchAgainst.getGroupB();
		//
		// List<IGCUnit> aGroupUnits = groupA.getAllUnits();
		// List<IGCUnit> bGroupUnits = groupB.getAllUnits();
		//
		// boolean isAGroup = groupId.equals(groupA.getGroupId());
		// // 获取自己所属的队伍，找到自己的索引
		// int mineIndex = 0;
		//
		// List<IGCUnit> checkGroupUnits = isAGroup ? aGroupUnits : bGroupUnits;
		// for (int i = 0, size = checkGroupUnits.size(); i < size; i++) {
		// IGCUnit unit = checkGroupUnits.get(i);
		// if (unit.getId().equals(userId)) {
		// mineIndex = i;
		// continue;
		// }
		// }

		return rsp.setIsSuccess(true).build().toByteString();
	}

	/**
	 * 填充失败消息
	 * 
	 * @param rsp
	 * @param tipMsg
	 * @return
	 */
	private ByteString fillFailMsg(GCBattleCommonRspMsg.Builder rsp, String tipMsg) {
		return rsp.setIsSuccess(false).setTipMsg(tipMsg).build().toByteString();
	}
}