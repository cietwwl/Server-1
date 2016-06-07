package com.rwbase.dao.groupsecret.pojo;

import java.util.List;

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
public class GroupSecretTeamInfoSynDataHolder {
	// private AtomicInteger version = new AtomicInteger();

	// private Map<String, AtomicInteger> versionMap = new HashMap<String, AtomicInteger>();
	//
	// /**
	// * 推送版本号
	// *
	// * @param id
	// * @param isGet
	// * @return
	// */
	// public int version(String id, boolean isGet) {
	// AtomicInteger synVersion = versionMap.get(id);
	// if (synVersion == null) {
	// synVersion = new AtomicInteger();
	// }
	//
	// if (isGet) {
	// return synVersion.get();
	// }
	//
	// return synVersion.incrementAndGet();
	// }

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

		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
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

		ClientDataSynMgr.synData(player, team, synType, eSynOpType.ADD_SINGLE);
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

		ClientDataSynMgr.synData(player, team, synType, eSynOpType.REMOVE_SINGLE);
	}

	// /**
	// * 同步更新一条记录
	// *
	// * @param player
	// * @param base
	// */
	// public void updateSingleData(Player player, SecretTeamInfoSynData team) {
	// if (team == null) {
	// return;
	// }
	//
	// ClientDataSynMgr.synData(player, team, synType, eSynOpType.UPDATE_SINGLE);
	// }
}