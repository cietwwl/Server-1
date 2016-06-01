package com.rw.service.groupsecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankAttribute;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.groupsecret.GroupSecretMatchEnemyDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.teamsyn.HeroLeftInfoSynData;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper.IUpdateSecretStateCallBack;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwproto.GroupSecretMatchProto.AttackEnemyStartReqMsg;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonRspMsg;
import com.rwproto.GroupSecretMatchProto.MatchRequestType;

/*
 * @author HC
 * @date 2016年6月1日 上午11:03:26
 * @Description 匹配的Handler
 */
public class GroupSecretMatchHandler {
	private static GroupSecretHandler handler = new GroupSecretHandler();

	public static GroupSecretHandler getHandler() {
		return handler;
	}

	public ByteString getSerchingEnemyHandler(Player player) {
		// 检查个人的帮派数据
		String userId = player.getUserId();

		GroupSecretMatchCommonRspMsg.Builder rsp = GroupSecretMatchCommonRspMsg.newBuilder();
		rsp.setReqType(MatchRequestType.SEARCHING_ENEMY);

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr userSecretBaseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = userSecretBaseDataMgr.get(userId);

		GroupSecretMatchEnemyDataMgr mgr = GroupSecretMatchEnemyDataMgr.getMgr();
		GroupSecretMatchEnemyData groupSecretMatchEnemyData = mgr.get(userId);
		if (groupSecretMatchEnemyData.isBeat()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "请先领取奖励");
			return rsp.build().toByteString();
		}

		// 如果没有打败，现在就要切换下状态
		IUpdateSecretStateCallBack call = new IUpdateSecretStateCallBack() {

			@Override
			public boolean call(GroupSecretMatchRankAttribute attr) {
				return attr.setNonBattleState();
			}
		};

		GroupSecretMatchHelper.updateGroupSecretState(GroupSecretHelper.generateCacheSecretId(groupSecretMatchEnemyData.getMatchUserId(), groupSecretMatchEnemyData.getId()), call);
		userSecretBaseDataMgr.updateMatchSecretId(player, null);// 更新匹配的数据
		// 更新秘境的敌人的信息
		mgr.clearMatchEnemyData(player);

		int matchTimes = userGroupSecretBaseData.getMatchTimes();
		int matchPrice = uniqueCfg.getMatchPrice(matchTimes);

		long coin = player.getReward(eSpecialItemId.Coin);
		if (matchPrice > coin) {// 金币数量不足
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "金币不足");
			return rsp.build().toByteString();
		}

		String matchId = GroupSecretMatchHelper.getGroupSecretMatchData(player);
		if (StringUtils.isEmpty(matchId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境，掠夺搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		String[] matchInfoArr = GroupSecretHelper.parseString2UserIdAndSecretId(matchId);
		String matchUserId = matchInfoArr[0];
		int id = Integer.parseInt(matchInfoArr[1]);

		UserCreateGroupSecretData useCreateData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		GroupSecretData groupSecretData = useCreateData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],查不着相应的秘境数据", matchId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境，掠夺搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceTemplate cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],秘境CfgId是[%s],找不到配置表", matchId, cfgId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境，掠夺搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		// 扣除费用
		player.getItemBagMgr().addItem(eSpecialItemId.Coin.getValue(), -matchPrice);

		// 获取可以掠夺的资源数量
		mgr.updateMatchEnemyData(player, groupSecretData, cfg);

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 挑战秘境的匹配敌人
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString attackEnemyStartHandler(Player player, AttackEnemyStartReqMsg req) {
		// 检查个人的帮派数据
		String userId = player.getUserId();

		GroupSecretMatchCommonRspMsg.Builder rsp = GroupSecretMatchCommonRspMsg.newBuilder();
		rsp.setReqType(MatchRequestType.ATTACK_ENEMY_START);

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("挑战秘境敌人", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("挑战秘境敌人", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("挑战秘境敌人", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		// 检查是否有敌人
		GroupSecretMatchEnemyData matchEnemyData = GroupSecretMatchEnemyDataMgr.getMgr().get(userId);
		if (StringUtils.isEmpty(matchEnemyData.getMatchUserId())) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您没有可以挑战的秘境");
			return rsp.build().toByteString();
		}

		// 检查是否敌人已经被击败
		if (matchEnemyData.isBeat()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "对手已经被打败，请领取奖励");
			return rsp.build().toByteString();
		}

		// 检查传递来请求攻打的矿点是否有人，或者是否被击败
		int index = req.getIndex().getNumber();
		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = matchEnemyData.getTeamAttrInfoMap(index);
		if (teamAttrInfoMap.isEmpty()) {
			GameLog.error("挑战秘境敌人", userId, String.format("角色挑战的驻守点[%s]没有任何人驻守", index));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您挑战的驻守点无人驻守");
			return rsp.build().toByteString();
		}

		// 检查剩余的血量信息
		List<String> armyInfoList = new ArrayList<String>(teamAttrInfoMap.size());

		PlayerIF readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(matchEnemyData.getMatchUserId());
		for (Entry<String, HeroLeftInfoSynData> e : teamAttrInfoMap.entrySet()) {
			String heroId = e.getKey();
			HeroIF hero = readOnlyPlayer.getHeroMgr().getHeroById(heroId);
			if (hero == null) {
				continue;
			}

			HeroLeftInfoSynData value = e.getValue();
			if (value != null) {
				if (value.getLife() > 0) {
				}
			} else {
			}
		}

		// 检查传递来的阵容信息

		return rsp.build().toByteString();
	}
}