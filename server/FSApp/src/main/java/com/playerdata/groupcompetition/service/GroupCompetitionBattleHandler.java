package com.playerdata.groupcompetition.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.battle.GCompMatchBattleCmdHelper;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompMatchDataHolder;
import com.playerdata.groupcompetition.holder.data.GCompMatchData;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.util.GCompBattleResult;
import com.playerdata.groupcompetition.util.GCompMatchConst.GCompMatchState;
import com.playerdata.groupcompetition.util.GCompMatchConst.GCompMatchType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleEndReqMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleReqType;
import com.rwproto.GroupCompetitionBattleProto.GCBattleResult;
import com.rwproto.GroupCompetitionBattleProto.GCBattleStartRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCMatchGroupInfo;
import com.rwproto.GroupCompetitionBattleProto.GCMatchGroupScoreRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCUploadHpInfoReqMsg;
import com.rwproto.MsgDef.Command;

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
		rsp.setReqType(GCBattleReqType.BATTLE_START);

		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {
			return fillFailMsg(rsp, "您没有帮派");
		}

		// 获取自己对垒的信息
		GCompEventsDataMgr instance = GCompEventsDataMgr.getInstance();
		IGCAgainst gcAgainstOfGroup = instance.getGCAgainstOfGroup(groupId, GroupCompetitionMgr.getInstance().getCurrentEventsType());
		if (gcAgainstOfGroup == null) {
			GCompUtil.log("请求开始战斗，找不到对垒信息，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "不能查找到对垒的信息");
		}

		GCompMatchDataHolder holder = GCompMatchDataHolder.getHolder();
		GCompMatchData matchData = holder.getMatchData(userId);
		if (matchData == null) {
			GCompUtil.log("请求开始战斗，matchData == null，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "您当前没有匹配到对手");
		}

		int matchState = matchData.getMatchState();
		if (matchState != GCompMatchState.START_BATTLE.state) {
			GCompUtil.log("请求开始战斗，matchState != GCompMatchState.START_BATTLE.state，角色名字{}，当前状态{}，角色Id{}", player.getUserName(), matchState, userId);
			return fillFailMsg(rsp, "当前不能进入战斗");
		}

		// 获取自己所属的队伍，找到自己的索引
		List<GCompTeamMember> members = matchData.getMyTeam().getMembers();
		int mineIndex = 0;
		GCompTeamMember mine = null;

		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member.getUserId().equals(userId)) {
				mine = member;
				mineIndex = i;
				break;
			}
		}

		if (mine == null) {
			GCompUtil.log("请求开始战斗，GCompTeamMember.mine == null，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "当前不能进入战斗");
		}

		if (mine.getResult() != GCompBattleResult.NonStart) {
			GCompUtil.log("请求开始战斗，mine.getResult() != GCompBattleResult.NonStart，重复进入战斗，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "不能重复进入战斗");
		}

		// 把敌人信息找出来
		// 先获取到我自己的上阵阵容
		ArmyInfoSimple mineArmyInfoSimple = mine.getArmyInfo();
		if (mineArmyInfoSimple == null) {
			GCompUtil.log("请求开始战斗，ArmyInfoSimple mineArmyInfoSimple == null，己方ArmyInfo为null，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "获取己方阵容信息失败");
		}

		// 获取到敌人的阵容
		GCompTeam enemyTeam = matchData.getEnemyTeam();
		if (enemyTeam == null) {
			GCompUtil.log("请求开始战斗，GCompTeam enemyTeam == null，敌方GCompTeam为null，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "获取敌方阵容信息失败");
		}

		List<GCompTeamMember> enemyMemberList = enemyTeam.getMembers();
		if (enemyMemberList == null || mineIndex >= enemyMemberList.size()) {
			GCompUtil.log("请求开始战斗，enemyMemberList == null || mineIndex >= enemyMemberList.size()，角色名字{}，角色Id{}", player.getUserName(), userId);
			return fillFailMsg(rsp, "获取敌方阵容信息失败");
		}

		ArmyInfoSimple enemyArmyInfoSimple = enemyMemberList.get(mineIndex).getArmyInfo();
		if (enemyArmyInfoSimple == null) {
			GameLog.error("帮派争霸开始战斗", userId + ">>>>" + player.getUserName(), "获取不到敌人对应的ArmyInfo信息");
			return fillFailMsg(rsp, "获取敌方阵容信息失败");
		}

		String mineArmyInfoJson;
		String enemyArmyInfoJson;
		try {
			mineArmyInfoJson = ArmyInfoHelper.getArmyInfo(mineArmyInfoSimple, true).toJson();
			enemyArmyInfoJson = ArmyInfoHelper.getArmyInfo(enemyArmyInfoSimple, true).toJson();
		} catch (Exception e) {
			GameLog.error("帮派争霸开始战斗", userId + ">>>>" + player.getUserName(), "转换敌我双方的ArmyInfoSimple到json出现异常", e);
			return fillFailMsg(rsp, "获取敌方阵容信息失败");
		}

		// 设置一下自己开始战斗
		mine.setResult(GCompBattleResult.Fighting);
		long now = System.currentTimeMillis();
		mine.setStartBattleTime(now);

		IGCGroup groupA = gcAgainstOfGroup.getGroupA();
		IGCGroup groupB = gcAgainstOfGroup.getGroupB();

		GCMatchGroupInfo.Builder matchGroupInfo = GCMatchGroupInfo.newBuilder();
		if (groupA.getGroupId().equals(groupId)) {// 如果我是A帮派
			fillMatchGroupInfo(matchGroupInfo, groupA, groupB);
		} else {
			fillMatchGroupInfo(matchGroupInfo, groupB, groupA);
		}

		GCBattleStartRspMsg.Builder battleStartRsp = GCBattleStartRspMsg.newBuilder();
		battleStartRsp.setMineArmyInfo(mineArmyInfoJson);
		battleStartRsp.setEnemyArmyInfo(enemyArmyInfoJson);
		battleStartRsp.setMatchGroupInfo(matchGroupInfo);
		rsp.setBattleStartRsp(battleStartRsp);

		// 战斗进入成功之后，把机器人的状态全部设置成战斗
		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member.isRobot() && member.getResult() == GCompBattleResult.NonStart && !member.getUserId().equals(userId)) {
				member.setResult(GCompBattleResult.Fighting);
				member.setStartBattleTime(now + 5000);// 机器人进入战斗时间延伸5秒，因为机器人也要模拟有加载时间限制
			}
		}

		return rsp.setIsSuccess(true).build().toByteString();
	}

	/**
	 * 上传英雄血量信息
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString uploadHpInfoHandler(Player player, GCUploadHpInfoReqMsg req) {
		String userId = player.getUserId();

		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		rsp.setReqType(GCBattleReqType.UPLOAD_HP_INFO);

		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {
			return rsp.setIsSuccess(true).build().toByteString();
		}

		GCompMatchDataHolder holder = GCompMatchDataHolder.getHolder();
		GCompMatchData matchData = holder.getMatchData(userId);
		if (matchData == null) {
			return rsp.setIsSuccess(true).build().toByteString();
		}

		// 个人匹配不需要这个信息
		if (matchData.getMatchType() == GCompMatchType.PERSONAL_MATCH.type) {
			return rsp.setIsSuccess(true).build().toByteString();
		}

		int matchState = matchData.getMatchState();
		if (matchState != GCompMatchState.START_BATTLE.state) {
			return rsp.setIsSuccess(true).build().toByteString();
		}

		// 获取自己所属的队伍，找到自己的索引
		List<GCompTeamMember> members = matchData.getMyTeam().getMembers();
		int mineIndex = 0;
		List<String> playerIdList = new ArrayList<String>();// 要同步给的PlayerIdList

		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member.isRobot()) {
				continue;
			}

			String id = member.getUserId();
			if (id.equals(userId)) {
				mineIndex = i;
				continue;
			}

			playerIdList.add(id);
		}

		// 如果有人
		if (!playerIdList.isEmpty()) {
			ByteString hpMsg = GCompMatchBattleCmdHelper.buildPushHpInfoMsg(mineIndex, req.getMineHpPercent(), req.getEnemyHpPercent());

			PlayerMgr playerMgr = PlayerMgr.getInstance();
			for (int i = 0, size = playerIdList.size(); i < size; i++) {
				Player p = playerMgr.find(playerIdList.get(i));
				if (p == null) {
					continue;
				}

				p.SendMsg(Command.MSG_GROUP_COMPETITION_BATTLE, hpMsg);
			}
		}

		return rsp.setIsSuccess(true).build().toByteString();
	}

	/**
	 * 获取对垒帮派的积分
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getMatchGroupScoreHandler(Player player) {
//		String userId = player.getUserId();

		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		rsp.setReqType(GCBattleReqType.MATCH_GROUP_SOCRE);

		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {
			return rsp.setIsSuccess(true).build().toByteString();
		}

		// 获取自己对垒的信息
		GCompEventsDataMgr instance = GCompEventsDataMgr.getInstance();
		IGCAgainst gcAgainstOfGroup = instance.getGCAgainstOfGroup(groupId, GroupCompetitionMgr.getInstance().getCurrentEventsType());
		if (gcAgainstOfGroup == null) {
			return rsp.setIsSuccess(true).build().toByteString();
		}

//		GCompMatchDataHolder holder = GCompMatchDataHolder.getHolder();
//		GCompMatchData matchData = holder.getMatchData(userId);
//		if (matchData == null) {
//			return rsp.setIsSuccess(true).build().toByteString();
//		}
//
//		int matchState = matchData.getMatchState();
//		if (matchState != GCompMatchState.START_BATTLE.state) {
//			return rsp.setIsSuccess(true).build().toByteString();
//		}

		IGCGroup groupA = gcAgainstOfGroup.getGroupA();
		IGCGroup groupB = gcAgainstOfGroup.getGroupB();

		GCMatchGroupScoreRspMsg.Builder groupScoreRsp = GCMatchGroupScoreRspMsg.newBuilder();
		if (groupA.getGroupId().equals(groupId)) {// 如果我是A帮派
			groupScoreRsp.setMyGroupScore(groupA.getGCompScore());
			groupScoreRsp.setEnemyGroupScore(groupB.getGCompScore());
		} else {
			groupScoreRsp.setMyGroupScore(groupB.getGCompScore());
			groupScoreRsp.setEnemyGroupScore(groupA.getGCompScore());
		}

		rsp.setMatchGroupScoreRsp(groupScoreRsp);
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
		rsp.setReqType(GCBattleReqType.BATTLE_END);

		String groupId = GroupHelper.getGroupId(player);
		if (StringUtils.isEmpty(groupId)) {
			GCompUtil.log("收到战斗结果，但是返现帮派id为空！成员的名字：{}", player.getUserName());
			return fillFailMsg(rsp, "您没有帮派");
		}

		GCompMatchDataHolder holder = GCompMatchDataHolder.getHolder();
		GCompMatchData matchData = holder.getMatchData(userId);
		if (matchData == null) {
			GCompUtil.log("收到战斗结果，matchData == null！成员的名字：{}", player.getUserName());
			return fillFailMsg(rsp, "当前阶段已经结束");
		}

		int matchState = matchData.getMatchState();
		if (matchState != GCompMatchState.START_BATTLE.state) {
			GCompUtil.log("收到战斗结果，matchState != GCompMatchState.START_BATTLE.state！成员的名字：{}，当前的state：{}", player.getUserName(), matchState);
			return fillFailMsg(rsp, "战斗未开始");
		}

		// 刷新一下战斗的结果
		GCBattleResult result = req.getResult();
		if (result == GCBattleResult.NONE) {
			GCompUtil.log("收到战斗结果，result == GCBattleResult.NONE！成员的名字：{}", player.getUserName());
			return fillFailMsg(rsp, "没有战斗结果");
		}

		GCompBattleResult battleResult = GCompBattleResult.Win;
		if (result == GCBattleResult.LOSE) {
			battleResult = GCompBattleResult.Lose;
		} else if (result == GCBattleResult.DRAW) {
			battleResult = GCompBattleResult.Draw;
		}

		holder.updateBattleResult(userId, battleResult);// 更新战斗状态

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

	/**
	 * 填充匹配的帮派消息
	 * 
	 * @param matchGroupInfo
	 * @param myGroup
	 * @param enemyGroup
	 */
	private void fillMatchGroupInfo(GCMatchGroupInfo.Builder matchGroupInfo, IGCGroup myGroup, IGCGroup enemyGroup) {
		matchGroupInfo.setMyGroupIcon(myGroup.getIcon());
		matchGroupInfo.setMyGroupName(myGroup.getGroupName());
		matchGroupInfo.setMyGroupScore(myGroup.getGCompScore());

		matchGroupInfo.setEnemyGroupIcon(enemyGroup.getIcon());
		matchGroupInfo.setEnemyGroupName(enemyGroup.getGroupName());
		matchGroupInfo.setEnemyGroupScore(enemyGroup.getGCompScore());
	}
}