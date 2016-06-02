package com.playerdata.groupsecret;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.groupsecret.pojo.UserGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年5月26日 下午4:53:33
 * @Description 
 */
public class UserGroupSecretBaseDataMgr {
	private static UserGroupSecretBaseDataMgr mgr = new UserGroupSecretBaseDataMgr();

	public static UserGroupSecretBaseDataMgr getMgr() {
		return mgr;
	}

	private UserGroupSecretBaseDataMgr() {
	}

	/**
	 * 获取秘境的数据
	 * 
	 * @param userId
	 * @return
	 */
	public UserGroupSecretBaseData get(String userId) {
		return UserGroupSecretDataHolder.getHolder().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		UserGroupSecretDataHolder.getHolder().updateData(userId);
	}

	/**
	 * 增加防守的秘境Id
	 * 
	 * @param userId
	 * @param id
	 */
	public void addDefendSecretId(String userId, String id) {
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.addDefendSecretId(id);
		update(userId);
	}

	/**
	 * 删除防守秘境的Id
	 * 
	 * @param id
	 * @param userId
	 */
	public void removeDefendSecretId(String userId, String id) {
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.removeDefendSecretId(id);
		update(userId);
	}

	/**
	 * 删除匹配到的敌人秘境Id
	 * 
	 * @param player 角色
	 * @param id 匹配到的秘境id
	 */
	public void updateMatchSecretId(Player player, String id) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		userGroupSecretBaseData.setMatchSecretId(id);
		update(userId);

		// 同步数据到前端
		synData(player);
	}

	/**
	 * 删除匹配到的敌人秘境Id
	 * 
	 * @param player 角色
	 * @param id 匹配到的秘境id
	 */
	public void updateMatchTimes(Player player) {
		String userId = player.getUserId();
		UserGroupSecretBaseData userGroupSecretBaseData = get(userId);
		if (userGroupSecretBaseData == null) {
			return;
		}

		int matchTimes = userGroupSecretBaseData.getMatchTimes();
		userGroupSecretBaseData.setMatchTimes(matchTimes + 1);
		update(userId);

		// 同步数据到前端
		synData(player);
	}

	private eSynType synType = eSynType.SECRETAREA_USER_INFO;

	/**
	 * 推送个人的帮派秘境数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		ClientDataSynMgr.synData(player, get(player.getUserId()), synType, eSynOpType.UPDATE_SINGLE);
	}
}