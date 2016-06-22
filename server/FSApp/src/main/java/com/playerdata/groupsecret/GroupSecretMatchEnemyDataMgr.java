package com.playerdata.groupsecret;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.manager.GameManager;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.teamsyn.HeroLeftInfoSynData;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.pojo.GroupSecretMatchEnemyDataHolder;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretLevelGetResTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwproto.GroupSecretMatchProto.HeroLeftInfo;

/*
 * @author HC
 * @date 2016年5月27日 上午11:08:33
 * @Description 
 */
public class GroupSecretMatchEnemyDataMgr {
	private static GroupSecretMatchEnemyDataMgr mgr = new GroupSecretMatchEnemyDataMgr();

	public static GroupSecretMatchEnemyDataMgr getMgr() {
		return mgr;
	}

	GroupSecretMatchEnemyDataMgr() {
	}

	/**
	 * 获取敌人的数据
	 * 
	 * @param userId
	 * @return
	 */
	public GroupSecretMatchEnemyData get(String userId) {
		return GroupSecretMatchEnemyDataHolder.getHolder().get(userId);
	}

	/**
	 * 清除个人的秘境匹配的数据
	 * 
	 * @param userId
	 */
	public void clearMatchEnemyData(Player player) {
		String userId = player.getUserId();
		GroupSecretMatchEnemyData enemyData = GroupSecretMatchEnemyDataHolder.getHolder().get(userId);
		if (enemyData == null) {
			return;
		}

		String id = GroupSecretHelper.generateCacheSecretId(enemyData.getMatchUserId(), enemyData.getId());
		enemyData.clearAllData();
		update(userId);

		removeData(player, id);
	}

	/**
	 * 更新匹配到的敌人的信息
	 * 
	 * @param player
	 * @param groupSecretData
	 * @param cfg
	 */
	public void updateMatchEnemyData(Player player, GroupSecretData groupSecretData, GroupSecretResourceCfg cfg, GroupSecretLevelGetResTemplate levelTmp, int zoneId, String zoneName) {
		String userId = player.getUserId();
		GroupSecretMatchEnemyData enemyData = get(userId);

		GameManager.getZoneId();

		int secretId = groupSecretData.getSecretId();
		long now = System.currentTimeMillis();
		enemyData.setId(groupSecretData.getId());
		enemyData.setMatchTime(now);
		enemyData.setMatchUserId(groupSecretData.getUserId());
		enemyData.setUserId(userId);
		enemyData.setCfgId(secretId);
		enemyData.setZoneId(zoneId);
		enemyData.setZoneName(zoneName);

		Enumeration<DefendUserInfoData> values = groupSecretData.getEnumerationValues();
		while (values.hasMoreElements()) {
			DefendUserInfoData nextElement = values.nextElement();

			long needTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getNeedTime());
			long changeTeamTime = nextElement.getChangeTeamTime();// 修改阵容时间
			int proRes = nextElement.getProRes() - nextElement.getRobRes();
			int proGE = nextElement.getProGE() - nextElement.getRobGE();
			int proGS = nextElement.getProGS() - nextElement.getRobGS();

			long createTime = groupSecretData.getCreateTime();
			boolean isFinish = now - createTime >= needTimeMillis;

			long minutes = TimeUnit.MILLISECONDS.toMinutes((isFinish ? (createTime + needTimeMillis) : now) - changeTeamTime);
			int fighting = nextElement.getFighting();
			proRes += (int) (fighting * levelTmp.getProductRatio() * minutes);
			proGE += (int) (levelTmp.getGroupExpRatio() * minutes);
			proGS += (int) (levelTmp.getGroupSupplyRatio() * minutes);

			// 产生的资源
			int robRes = proRes * levelTmp.getRobRatio() / AttributeConst.DIVISION;
			int robGE = proGE * levelTmp.getRobGERatio() / AttributeConst.DIVISION;
			int robGS = proGS * levelTmp.getRobGSRatio() / AttributeConst.DIVISION;

			int index = nextElement.getIndex();
			enemyData.setRobResValue(index, robRes);
			enemyData.setRobGSValue(index, robGS);
			enemyData.setRobGEValue(index, robGE);

			List<String> heroList = nextElement.getHeroList();
			int size = heroList.size();

			Map<String, HeroLeftInfoSynData> map = new HashMap<String, HeroLeftInfoSynData>(size);
			for (int i = 0; i < size; i++) {
				map.put(heroList.get(i), null);
			}

			enemyData.initHeroLeftInfo(index, map);
		}

		enemyData.updateVersion();
		update(userId);
	}

	/**
	 * 设置攻击时间
	 * 
	 * @param player
	 * @param time
	 */
	public void updateMatchState2Atk(Player player, long time) {
		String userId = player.getUserId();
		GroupSecretMatchEnemyData enemyData = get(userId);
		enemyData.setAtkTime(time);

		enemyData.updateVersion();
		update(userId);

		// updateSingleData(player);
	}

	/**
	 * 更新阵容信息
	 * 
	 * @param player
	 * @param index
	 * @param leftList
	 * @return 是否击败了整个秘境敌人
	 */
	public boolean updateDefendIndexHeroLeftInfo(Player player, int index, List<HeroLeftInfo> leftList) {
		if (leftList.isEmpty()) {
			return false;
		}

		String userId = player.getUserId();
		GroupSecretMatchEnemyData enemyData = get(userId);
		if (enemyData == null) {
			return false;
		}

		String matchUserId = enemyData.getMatchUserId();
		UserCreateGroupSecretData userCreateGroupSecretData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		if (userCreateGroupSecretData == null) {
			return false;
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(enemyData.getId());
		if (groupSecretData == null) {
			return false;
		}

		DefendUserInfoData defendUserInfoData = groupSecretData.getDefendUserInfoData(index);
		if (defendUserInfoData == null) {
			return false;
		}

		PlayerIF readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(defendUserInfoData.getUserId());

		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = enemyData.getTeamAttrInfoMap(index);

		for (int i = 0, size = leftList.size(); i < size; i++) {
			HeroLeftInfo leftInfo = leftList.get(i);
			if (leftInfo == null) {
				continue;
			}

			String heroId = leftInfo.getId();
			HeroIF hero = readOnlyPlayer.getHeroMgr().getHeroById(heroId);
			if (hero == null) {
				continue;
			}

			HeroLeftInfoSynData heroLeftInfoSynData = teamAttrInfoMap.get(heroId);
			int leftLife = leftInfo.getLeftLife();
			if (heroLeftInfoSynData == null) {
				AttrData totalData = hero.getAttrMgr().getRoleAttrData().getTotalData();
				enemyData.updateHeroLeftInfo(index, heroId, new HeroLeftInfoSynData(leftLife, leftInfo.getLeftEnergy(), totalData.getLife(), totalData.getEnergy()));
			} else {
				int maxLife = heroLeftInfoSynData.getMaxLife();
				enemyData.updateHeroLeftInfo(index, heroId, new HeroLeftInfoSynData(leftLife, leftInfo.getLeftEnergy(), maxLife, heroLeftInfoSynData.getMaxEnergy()));
			}
		}

		enemyData.setAttackTimes(index);// 更新攻击波数

		boolean isBeat = false;
		if (!enemyData.checkHasHeroAlive()) {
			enemyData.setBeat(isBeat = true);
		}

		enemyData.updateVersion();
		update(userId);

		// // 同步数据
		// updateSingleData(player);
		return isBeat;
	}

	/**
	 * 同步个人的秘境数据到数据库
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		GroupSecretMatchEnemyDataHolder.getHolder().updateData(userId);
	}

	// /**
	// * 同步秘境的数据
	// *
	// * @param player
	// */
	// private void updateSingleData(Player player) {
	// GroupSecretDataSynData info = GroupSecretHelper.fillMatchSecretInfo(player);
	//
	// // 同步数据
	// SecretBaseInfoSynData base = info.getBase();
	// if (base != null) {
	// player.getBaseHolder().updateSingleData(player, base);
	// }
	//
	// SecretTeamInfoSynData team = info.getTeam();
	// if (team == null) {
	// player.getTeamHolder().updateSingleData(player, team);
	// }
	// }

	/**
	 * 移除秘境的数据
	 * 
	 * @param player
	 * @param id
	 */
	private void removeData(Player player, String id) {
		// 同步数据
		player.getBaseHolder().removeData(player, new SecretBaseInfoSynData(id, 0, true, 0, 0, 0, 0, 0, 0, ""));
		player.getTeamHolder().removeData(player, new SecretTeamInfoSynData(id, null, 0));
	}
}