package com.rwbase.dao.groupsecret.pojo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年5月30日 下午6:07:45
 * @Description 
 */
public class SecretTeamInfoSynDataHolder {
	private static SecretTeamInfoSynDataHolder holder = new SecretTeamInfoSynDataHolder();

	private AtomicInteger version = new AtomicInteger();

	public static SecretTeamInfoSynDataHolder getHolder() {
		return holder;
	}

	SecretTeamInfoSynDataHolder() {
	}

	private eSynType synType = eSynType.SECRETAREA_DEFEND_TEAM_INFO;

	/**
	 * 同步所有的数据
	 * 
	 * @param player
	 * @param list
	 */
	public void synAllData(Player player, List<SecretTeamInfoSynData> list) {
		if (list == null || list.isEmpty()) {
			return;
		}

		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST, version.get());
	}

	/**
	 * 同步增加一条记录
	 * 
	 * @param player
	 * @param team
	 */
	public void addData(Player player, SecretTeamInfoSynData team) {
		if (team == null) {
			return;
		}

		ClientDataSynMgr.synData(player, team, synType, eSynOpType.ADD_SINGLE, version.get());
	}

	/**
	 * 同步删除一条记录
	 * 
	 * @param player
	 * @param team
	 */
	public void removeData(Player player, SecretTeamInfoSynData team) {
		if (team == null) {
			return;
		}

		ClientDataSynMgr.synData(player, team, synType, eSynOpType.REMOVE_SINGLE, version.get());
	}
}