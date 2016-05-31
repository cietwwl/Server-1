package com.playerdata.groupsecret;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.groupsecret.pojo.GroupSecretTeamDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年5月27日 下午9:28:03
 * @Description 
 */
public class GroupSecretTeamDataMgr {

	private static GroupSecretTeamDataMgr mgr = new GroupSecretTeamDataMgr();

	public static GroupSecretTeamDataMgr getMgr() {
		return mgr;
	}

	GroupSecretTeamDataMgr() {
	}

	/**
	 * 获取阵容信息
	 * 
	 * @param userId
	 * @return
	 */
	public GroupSecretTeamData get(String userId) {
		return GroupSecretTeamDataHolder.getHolder().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		GroupSecretTeamDataHolder.getHolder().updateData(userId);
	}

	/**
	 * 增加防守阵容信息
	 * 
	 * @param player
	 * @param canAddDefendList
	 */
	public void addDefendHeroIdList(Player player, List<String> canAddDefendList) {
		String userId = player.getUserId();
		GroupSecretTeamData groupSecretTeamData = get(userId);
		if (groupSecretTeamData == null) {
			return;
		}

		groupSecretTeamData.addDefendHeroIdList(canAddDefendList);
		update(userId);

		// 同步数据
		synData(player);
	}

	/**
	 * 删除使用的一些英雄Id
	 * 
	 * @param player
	 * @param removeList
	 */
	public void removeTeamHeroList(Player player, List<String> removeList) {
		String userId = player.getUserId();
		GroupSecretTeamData groupSecretTeamData = get(userId);
		if (groupSecretTeamData == null) {
			return;
		}

		groupSecretTeamData.removeDefendHeroIdList(removeList, userId);
		update(userId);

		// 同步数据
		synData(player);
	}

	/**
	 * 更换阵容信息
	 * 
	 * @param player
	 * @param changeList
	 */
	public void changeTeamHeroList(Player player, List<String> changeList) {
		String userId = player.getUserId();
		GroupSecretTeamData groupSecretTeamData = get(userId);
		if (groupSecretTeamData == null) {
			return;
		}

		groupSecretTeamData.changeTeamHeroList(changeList, userId);
		update(userId);

		// 同步数据
		synData(player);
	}

	private eSynType synType = eSynType.SECRETAREA_TEAM_INFO;

	/**
	 * 推送数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		ClientDataSynMgr.synData(player, get(player.getUserId()), synType, eSynOpType.UPDATE_SINGLE);
	}
}