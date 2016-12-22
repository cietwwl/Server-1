package com.rw.service.groupsecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.groupSecret.GroupSecretBM;
import com.bm.login.ZoneBM;
import com.bm.rank.RankType;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankAttribute;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankComparable;
import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupsecret.GroupSecretMatchEnemyDataMgr;
import com.playerdata.groupsecret.GroupSecretTeamDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.manager.GameManager;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.attrdata.AttrData;
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
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretLevelGetResTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretLevelGetResCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwbase.dao.groupsecret.pojo.db.data.HeroInfoData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwproto.BattleCommon;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.GroupSecretMatchProto.AttackEnemyEndReqMsg;
import com.rwproto.GroupSecretMatchProto.AttackEnemyStartReqMsg;
import com.rwproto.GroupSecretMatchProto.AttackEnemyStartRspMsg;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonRspMsg;
import com.rwproto.GroupSecretMatchProto.MatchRequestType;
import com.rwproto.GroupSecretMatchProto.SearchingSecretRspMsg;

/*
 * @author HC
 * @date 2016年6月1日 上午11:03:26
 * @Description 匹配的Handler
 */
public class GroupSecretMatchHandler {
	private static GroupSecretMatchHandler handler = new GroupSecretMatchHandler();

	public static GroupSecretMatchHandler getHandler() {
		return handler;
	}

	/**
	 * 匹配敌人数据
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getSerchingEnemyHandler(Player player) {
		// 检查个人的帮派数据
		String userId = player.getUserId();

		GroupSecretMatchCommonRspMsg.Builder rsp = GroupSecretMatchCommonRspMsg.newBuilder();
		rsp.setReqType(MatchRequestType.SEARCHING_ENEMY);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "加入帮派才能进行该操作");
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
		// 清除敌人信息
		GroupSecretBM.clearMatchEnemyInfo(player);

		int matchTimes = userGroupSecretBaseData.getMatchTimes();
		int matchPrice = uniqueCfg.getMatchPrice(matchTimes);

		long coin = player.getReward(eSpecialItemId.Coin);
		if (matchPrice > coin) {// 金币数量不足
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "金币不足");
			return rsp.build().toByteString();
		}

		String matchId = GroupSecretMatchHelper.getGroupSecretMatchData(player);
		if (StringUtils.isEmpty(matchId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境");
			return rsp.build().toByteString();
		}

		String[] matchInfoArr = GroupSecretHelper.parseString2UserIdAndSecretId(matchId);
		String matchUserId = matchInfoArr[0];
		int id = Integer.parseInt(matchInfoArr[1]);

		UserCreateGroupSecretData useCreateData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		GroupSecretData groupSecretData = useCreateData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],查不着相应的秘境数据", matchId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],秘境CfgId是[%s],找不到配置表", matchId, cfgId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境");
			return rsp.build().toByteString();
		}

		int level = player.getLevel();
		GroupSecretLevelGetResTemplate levelGetResTemplate = GroupSecretLevelGetResCfgDAO.getCfgDAO().getLevelGetResTemplate(cfg.getLevelGroupId(), level);
		if (levelGetResTemplate == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("找不到等级组[%s],角色等级[%s]的配置表", cfg.getLevelGroupId(), level));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到秘境等级组对应的配置");
			return rsp.build().toByteString();
		}

		// 获取zone信息
		int zoneId = GameManager.getZoneId();
		String zoneName = "";
		TableZoneInfo tableZoneInfo = ZoneBM.getInstance().getTableZoneInfo(zoneId);
		if (tableZoneInfo != null) {
			zoneName = tableZoneInfo.getZoneName();
		}

		// 扣除费用
		ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Coin.getValue(), -matchPrice);

		// 获取可以掠夺的资源数量
		mgr.updateMatchEnemyData(player, groupSecretData, cfg, levelGetResTemplate, zoneId, zoneName, groupId);

		// 设置角色匹配到的秘境数据
		userSecretBaseDataMgr.updateMatchSecretId(player, matchId);

		SearchingSecretRspMsg.Builder rspMsg = SearchingSecretRspMsg.newBuilder();
		rspMsg.setId(matchId);

		rsp.setIsSuccess(true);
		rsp.setSearchingRspMsg(rspMsg);
		rsp.setTipMsg("找到一处其他帮派驻守的藏宝洞");
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

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查是否有敌人
		GroupSecretMatchEnemyDataMgr enemyDataMgr = GroupSecretMatchEnemyDataMgr.getMgr();
		GroupSecretMatchEnemyData matchEnemyData = enemyDataMgr.get(userId);
		String matchUserId = matchEnemyData.getMatchUserId();
		if (StringUtils.isEmpty(matchUserId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您没有可以挑战的秘境");
			return rsp.build().toByteString();
		}

		// 检查是否敌人已经被击败
		if (matchEnemyData.isBeat()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "对手已经被打败，请领取奖励");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr userSecretBaseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = userSecretBaseDataMgr.get(userId);

		int secretId = matchEnemyData.getId();
		String id = GroupSecretHelper.generateCacheSecretId(matchUserId, secretId);
		// 检查是不是自己主动搁置到战斗超时
		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		UserCreateGroupSecretData useCreateData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		GroupSecretData groupSecretData = useCreateData.getGroupSecretData(secretId);
		if (groupSecretData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],查不着相应的秘境数据", id));
			GroupSecretBM.clearMatchEnemyInfo(player);

			GroupSecretHelper.fillMatchRspInfo(rsp, false, "秘境状态错误");
			return rsp.build().toByteString();
		}

		int index = req.getIndex().getNumber();
		DefendUserInfoData defendUserInfoData = groupSecretData.getDefendUserInfoData(index);
		if (defendUserInfoData == null) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "此驻守点没人驻守");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],秘境CfgId是[%s],找不到配置表", id, cfgId));
			GroupSecretBM.clearMatchEnemyInfo(player);

			GroupSecretHelper.fillMatchRspInfo(rsp, false, "秘境状态错误");
			return rsp.build().toByteString();
		}

		// 检查需要的钥石数量够不够
		int robNeedKeyNum = cfg.getRobNeedKeyNum();
		if (robNeedKeyNum > userGroupSecretBaseData.getKeyCount()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "钥石数量不足");
			return rsp.build().toByteString();
		}

		// 免战超时
		long now = System.currentTimeMillis();
		long atkTime = matchEnemyData.getAtkTime();
		if (atkTime <= 0) {
			long matchTime = matchEnemyData.getMatchTime();
			if (now - matchTime >= TimeUnit.MINUTES.toMillis(uniqueCfg.getMatchNonBattleTime())) {
				GroupSecretBM.clearMatchEnemyInfo(player);

				GroupSecretHelper.fillMatchRspInfo(rsp, false, "秘境状态错误");
				return rsp.build().toByteString();
			}
		} else {
			if (now - atkTime >= TimeUnit.MINUTES.toMillis(cfg.getProtectTime())) {
				GroupSecretBM.clearMatchEnemyInfo(player);

				GroupSecretHelper.fillMatchRspInfo(rsp, false, "未在可攻击时间内完成掠夺");
				return rsp.build().toByteString();
			}
		}

		int matchPrice = uniqueCfg.getMatchPrice(userGroupSecretBaseData.getMatchTimes());

		// 检查当前的秘境攻击人是不是自己
		Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);
		RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(id);
		if (rankingEntry == null) {
			GroupSecretBM.clearMatchEnemyInfo(player);
			// 第三步返回钱
			ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Coin.getValue(), matchPrice);

			GameLog.error("挑战秘境敌人", userId, String.format("从匹配排行榜中找不到对应[%s]的记录", id));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "对方已被其他玩家挑战，搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		// 获取当前秘境的数据
		if (!rankingEntry.getExtendedAttribute().setFightingState(userId, now)) {
			GroupSecretBM.clearMatchEnemyInfo(player);
			// 第三步返回钱
			ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Coin.getValue(), matchPrice);

			GameLog.error("挑战秘境敌人", userId, String.format("从匹配排行榜中找不到对应[%s]的记录,获取的攻击对象不是自己或者状态为不可攻击", id));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "对方已被其他玩家挑战，搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		ranking.subimitUpdatedTask(rankingEntry);

		// 检查个人传递来的阵容信息
		List<BattleHeroPosition> teamHeroList = req.getHeroListList();
		if (teamHeroList.isEmpty()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "攻击阵容不能为空");
			return rsp.build().toByteString();
		}

		boolean hasMainRole = false;
		GroupSecretTeamData groupSecretTeamData = GroupSecretTeamDataMgr.getMgr().get(userId);
		for (int i = 0, size = teamHeroList.size(); i < size; i++) {
			BattleHeroPosition heroPos = teamHeroList.get(i);
			String heroId = heroPos.getHeroId();
			// Hero hero = player.getHeroMgr().getHeroById(heroId);
			Hero hero = player.getHeroMgr().getHeroById(player, heroId);
			if (hero == null) {
				GroupSecretHelper.fillMatchRspInfo(rsp, false, "英雄状态错误");
				return rsp.build().toByteString();
			}

			if (heroId.equals(userId)) {
				hasMainRole = true;
				continue;
			}

			if (groupSecretTeamData.checkHeroIsDie(heroId)) {
				GameLog.error("挑战秘境敌人", userId, String.format("Id为[%s]的英雄已经死亡了，客户端却还是请求其出战", heroId));
				GroupSecretHelper.fillMatchRspInfo(rsp, false, "已经死亡的英雄不能出战");
				return rsp.build().toByteString();
			}
		}

		if (!hasMainRole) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "主角必须要出战");
			return rsp.build().toByteString();
		}

		// 检查传递来请求攻打的矿点是否有人，或者是否被击败
		Map<String, HeroInfoData> teamAttrInfoMap = matchEnemyData.getTeamAttrInfoMap(index);
		if (teamAttrInfoMap.isEmpty()) {
			GameLog.error("挑战秘境敌人", userId, String.format("角色挑战的驻守点[%s]没有任何人驻守", index));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您挑战的驻守点无人驻守");
			return rsp.build().toByteString();
		}

		// 检查剩余的血量信息
		String defendUserId = defendUserInfoData.getUserId();
		boolean hasAlive = false;
		int teamSize = teamAttrInfoMap.size();
		List<String> canHeroList = new ArrayList<String>(teamSize);
		// Map<String, CurAttrData> curAttrData = new HashMap<String, CurAttrData>(teamSize);
		for (Entry<String, HeroInfoData> e : teamAttrInfoMap.entrySet()) {
			String heroId = e.getKey();

			boolean isMainRole = defendUserId.equals(heroId);

			HeroInfoData heroInfoData = e.getValue();
			HeroLeftInfoSynData value = heroInfoData.getLeft();
			if (value == null) {
				if (!isMainRole) {
					canHeroList.add(heroId);
				}
				hasAlive = true;
			} else {
				if (value.getLife() > 0) {
					if (!isMainRole) {
						canHeroList.add(heroId);
					}
					hasAlive = true;
				}
			}
		}

		if (!hasAlive) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "此驻守点已经被击败");
			return rsp.build().toByteString();
		}

		// 填充ArmyInfo信息
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(defendUserId, canHeroList);
		String mainRoleId = armyInfo.getPlayer().getRoleBaseInfo().getId();

		HeroInfoData heroData = teamAttrInfoMap.get(mainRoleId);
		HeroLeftInfoSynData left = heroData.getLeft();

		AttrData attrData = armyInfo.getPlayer().getAttrData();

		CurAttrData leftInfo = new CurAttrData();
		leftInfo.setId(mainRoleId);
		if (left == null) {
			leftInfo.setCurLife(attrData.getLife());
		} else {
			leftInfo.setCurLife(left.getLife());
			leftInfo.setCurEnergy(left.getEnergy());
		}

		leftInfo.setMaxLife(attrData.getLife());

		armyInfo.getPlayer().setCurAttrData(leftInfo);

		List<ArmyHero> heroList = armyInfo.getHeroList();
		for (int i = 0, size = heroList.size(); i < size; i++) {
			ArmyHero armyHero = heroList.get(i);
			String heroId = armyHero.getRoleBaseInfo().getId();

			heroData = teamAttrInfoMap.get(heroId);
			left = heroData.getLeft();

			attrData = armyHero.getAttrData();

			CurAttrData leftAttrData = new CurAttrData();
			leftAttrData.setId(heroId);
			if (left == null) {
				leftAttrData.setCurLife(attrData.getLife());
			} else {
				leftAttrData.setCurLife(left.getLife());
				leftAttrData.setCurEnergy(left.getEnergy());
			}

			leftAttrData.setMaxLife(attrData.getLife());

			armyHero.setPosition(heroData.getPos());
			armyHero.setCurAttrData(leftAttrData);
		}

		AttackEnemyStartRspMsg.Builder endRsp = AttackEnemyStartRspMsg.newBuilder();
		try {
			endRsp.setArmyInfo(armyInfo.toJson());
		} catch (Exception e1) {
			GameLog.error("挑战秘境敌人", userId, "转换ArmyInfo到json出现了异常", e1);
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您挑战的驻守点无人驻守");
			return rsp.build().toByteString();
		}

		userSecretBaseDataMgr.updateReceiveKeyCount(player, -robNeedKeyNum, atkTime <= 0);

		// 更新秘境匹配的次数
		if (atkTime <= 0) {
			// 更新秘境的攻打时间
			enemyDataMgr.updateMatchState2Atk(player, now);
		}

		// 更新阵容
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, BattleCommon.eBattlePositionType.GroupSecretPos_VALUE, id, EmbattlePositonHelper.parseMsgHeroPos2Memery(teamHeroList));

		rsp.setIsSuccess(true);
		rsp.setTipMsg("");
		rsp.setAttackStartRsp(endRsp);
		return rsp.build().toByteString();
	}

	/**
	 * 挑战敌人结束
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString attackEnemyEndHandler(Player player, AttackEnemyEndReqMsg req) {
		// 检查个人的帮派数据
		String userId = player.getUserId();

		GroupSecretMatchCommonRspMsg.Builder rsp = GroupSecretMatchCommonRspMsg.newBuilder();
		rsp.setReqType(MatchRequestType.ATTACK_ENEMY_END);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查是否有敌人
		GroupSecretMatchEnemyDataMgr enemyDataMgr = GroupSecretMatchEnemyDataMgr.getMgr();
		GroupSecretMatchEnemyData matchEnemyData = enemyDataMgr.get(userId);
		String matchUserId = matchEnemyData.getMatchUserId();
		if (StringUtils.isEmpty(matchUserId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您没有可以挑战的秘境");
			return rsp.build().toByteString();
		}

		// 检查是否敌人已经被击败
		if (matchEnemyData.isBeat()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "对手已经被打败，请领取奖励");
			return rsp.build().toByteString();
		}

		int secretId = matchEnemyData.getId();
		String id = GroupSecretHelper.generateCacheSecretId(matchUserId, secretId);
		// 检查是不是自己主动搁置到战斗超时
		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		UserCreateGroupSecretData useCreateData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		GroupSecretData groupSecretData = useCreateData.getGroupSecretData(secretId);
		if (groupSecretData == null) {
			GameLog.error("挑战秘境敌人结束", userId, String.format("匹配到的记录Id是[%s],查不着相应的秘境数据", id));
			GroupSecretBM.clearMatchEnemyInfo(player);

			GroupSecretHelper.fillMatchRspInfo(rsp, false, "秘境状态错误");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GameLog.error("挑战秘境敌人结束", userId, String.format("匹配到的记录Id是[%s],秘境CfgId是[%s],找不到配置表", id, cfgId));
			GroupSecretBM.clearMatchEnemyInfo(player);

			GroupSecretHelper.fillMatchRspInfo(rsp, false, "秘境状态错误");
			return rsp.build().toByteString();
		}

		// 免战超时
		long now = System.currentTimeMillis();
		long atkTime = matchEnemyData.getAtkTime();
		if (atkTime <= 0) {
			long matchTime = matchEnemyData.getMatchTime();
			if (now - matchTime >= TimeUnit.MINUTES.toMillis(uniqueCfg.getMatchNonBattleTime())) {
				GroupSecretBM.clearMatchEnemyInfo(player);

				GroupSecretHelper.fillMatchRspInfo(rsp, false, "秘境状态错误");
				return rsp.build().toByteString();
			}
		}

		// 攻击阵容信息
		GroupSecretTeamDataMgr.getMgr().updateHeroLeftInfo(player, req.getMyLeftList());

		// 更新敌人防守阵容信息
		boolean isBeat = enemyDataMgr.updateDefendIndexHeroLeftInfo(player, req.getIndex().getNumber(), req.getEnemyLeftList());

		// 后续要通知所有的相关秘境被掠夺的资源数量
		if (isBeat) {// 打败了
			String groupName = "";
			UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
			Group group = GroupBM.get(userGroupAttributeData.getGroupId());
			if (group != null) {
				GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
				if (groupData != null) {
					groupName = groupData.getGroupName();
				}
			}

			PlayerIF readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(matchUserId);
			UserCreateGroupSecretDataMgr.getMgr().updateGroupSecretRobInfo(matchUserId, readOnlyPlayer.getLevel(), secretId, matchEnemyData.getRobRes(), matchEnemyData.getRobGS(), matchEnemyData.getRobGE(), matchEnemyData.getAtkTimes(), groupName, player.getUserName(), matchEnemyData.getZoneId(),
					matchEnemyData.getZoneName());
		}

		// 通知角色日常任务 by Alex
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.GROUPSERCET_BATTLE, 1);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.GROUPSERCET_SCAN, 1);

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 领取掠夺的秘境奖励
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getRobRewardHandler(Player player) {
		// 检查个人的帮派数据
		String userId = player.getUserId();

		GroupSecretMatchCommonRspMsg.Builder rsp = GroupSecretMatchCommonRspMsg.newBuilder();
		rsp.setReqType(MatchRequestType.GET_REWARD);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查是否有敌人
		GroupSecretMatchEnemyDataMgr enemyDataMgr = GroupSecretMatchEnemyDataMgr.getMgr();
		GroupSecretMatchEnemyData matchEnemyData = enemyDataMgr.get(userId);
		if (StringUtils.isEmpty(matchEnemyData.getMatchUserId())) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "当前您没有可以领取的掠夺奖励");
			return rsp.build().toByteString();
		}

		// 检查是否敌人已经被击败
		if (!matchEnemyData.isBeat()) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "暂未击败掠夺秘境全体驻守成员，不可领奖");
			return rsp.build().toByteString();
		}

		int robRes = matchEnemyData.getAllRobResValue();
		// 增加帮派经验物资
		boolean hasGroupAdd = false;
		UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
		String groupId = userGroupAttributeData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			if (groupId.equals(matchEnemyData.getGroupId())) {// 当前领取这一刻的帮派Id跟我匹配到这个秘境时的帮派Id是不是一个
				Group group = GroupBM.get(groupId);
				if (group != null) {
					hasGroupAdd = true;
					group.getGroupBaseDataMgr().updateGroupDonate(player, null, matchEnemyData.getAllRobGSValue(), matchEnemyData.getAllRobGEValue(), 0, true);
				}
			}
		}

		// 增加资源
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(matchEnemyData.getCfgId());
		if (cfg != null && robRes > 0) {
			ItemBagMgr.getInstance().addItem(player, cfg.getReward(), robRes);
		}

		if (cfg != null) {
			int level = player.getLevel();
			GroupSecretLevelGetResTemplate levelGetResTemplate = GroupSecretLevelGetResCfgDAO.getCfgDAO().getLevelGetResTemplate(cfg.getLevelGroupId(), level);
			if (levelGetResTemplate != null) {
				ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Gold.getValue(), levelGetResTemplate.getRobDiamond());
			}
		}

		// 移除匹配的敌人信息
		GroupSecretBM.clearMatchEnemyInfo(player);

		rsp.setIsSuccess(true);
		if (!hasGroupAdd) {
			rsp.setTipMsg("因退出原帮派，不能获得该秘境帮派物资和帮派经验");
		}
		return rsp.build().toByteString();
	}
}