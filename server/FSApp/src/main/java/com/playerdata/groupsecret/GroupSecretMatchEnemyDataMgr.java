package com.playerdata.groupsecret;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.teamsyn.HeroLeftInfoSynData;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.pojo.GroupSecretMatchEnemyDataHolder;
import com.rwbase.dao.groupsecret.pojo.SecretBaseInfoSynDataHolder;
import com.rwbase.dao.groupsecret.pojo.SecretTeamInfoSynDataHolder;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.GroupSecretDataSynData;

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

		String id = GroupSecretHelper.generateCacheSecretId(userId, enemyData.getId());
		enemyData.clearAllData();
		update(userId);

		// 同步数据
		SecretBaseInfoSynDataHolder.getHolder().removeData(player, new SecretBaseInfoSynData(id, 0, true, 0, 0, 0, 0, 0, 0));
		SecretTeamInfoSynDataHolder.getHolder().removeData(player, new SecretTeamInfoSynData(id, null));
	}

	/**
	 * 更新匹配到的敌人的信息
	 * 
	 * @param player
	 * @param groupSecretData
	 * @param cfg
	 */
	public void updateMatchEnemyData(Player player, GroupSecretData groupSecretData, GroupSecretResourceTemplate cfg) {
		String userId = player.getUserId();
		GroupSecretMatchEnemyData enemyData = get(userId);

		enemyData.setId(groupSecretData.getId());
		enemyData.setMatchUserId(groupSecretData.getUserId());
		enemyData.setUserId(userId);

		Enumeration<DefendUserInfoData> values = groupSecretData.getEnumerationValues();
		while (values.hasMoreElements()) {
			DefendUserInfoData nextElement = values.nextElement();
			// 产生的资源
			int robRes = nextElement.getRobRes() * cfg.getRobGERatio() / AttributeConst.DIVISION;
			int robGS = nextElement.getRobGS() * cfg.getRobGSRatio() / AttributeConst.DIVISION;
			int proGE = nextElement.getProGE() * cfg.getRobGERatio() / AttributeConst.DIVISION;

			int index = nextElement.getIndex();
			enemyData.setRobResValue(index, robRes);
			enemyData.setRobGSValue(index, robGS);
			enemyData.setRobGEValue(index, proGE);

			List<String> heroList = nextElement.getHeroList();
			int size = heroList.size();

			Map<String, HeroLeftInfoSynData> map = new HashMap<String, HeroLeftInfoSynData>(size);
			for (int i = 0; i < size; i++) {
				map.put(heroList.get(i), null);
			}

			enemyData.initHeroLeftInfo(index, map);
		}

		update(userId);

		GroupSecretDataSynData info = GroupSecretHelper.fillMatchSecretInfo(player);

		// 同步数据
		SecretBaseInfoSynData base = info.getBase();
		if (base != null) {
			SecretBaseInfoSynDataHolder.getHolder().addData(player, base);
		}

		SecretTeamInfoSynData team = info.getTeam();
		if (team == null) {
			SecretTeamInfoSynDataHolder.getHolder().addData(player, team);
		}
	}

	/**
	 * 同步个人的秘境数据到数据库
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		GroupSecretMatchEnemyDataHolder.getHolder().updateData(userId);
	}
}