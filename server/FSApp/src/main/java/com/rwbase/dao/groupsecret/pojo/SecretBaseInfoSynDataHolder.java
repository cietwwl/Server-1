package com.rwbase.dao.groupsecret.pojo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年5月30日 下午6:07:28
 * @Description 
 */
public class SecretBaseInfoSynDataHolder {
	private static SecretBaseInfoSynDataHolder holder = new SecretBaseInfoSynDataHolder();

	private AtomicInteger version = new AtomicInteger();

	public static SecretBaseInfoSynDataHolder getHolder() {
		return holder;
	}

	SecretBaseInfoSynDataHolder() {
	}

	private eSynType synType = eSynType.SECRETAREA_BASE_INFO;

	/**
	 * 同步所有的数据
	 * 
	 * @param player
	 * @param list
	 */
	public void synAllData(Player player, List<SecretBaseInfoSynData> list) {
		if (list == null || list.isEmpty()) {
			return;
		}

		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST, version.get());
	}

	/**
	 * 同步增加一条记录
	 * 
	 * @param player
	 * @param base
	 */
	public void addData(Player player, SecretBaseInfoSynData base) {
		if (base == null) {
			return;
		}

		ClientDataSynMgr.synData(player, base, synType, eSynOpType.ADD_SINGLE, version.get());
	}

	/**
	 * 同步删除一条记录
	 * 
	 * @param player
	 * @param base
	 */
	public void removeData(Player player, SecretBaseInfoSynData base) {
		if (base == null) {
			return;
		}

		ClientDataSynMgr.synData(player, base, synType, eSynOpType.REMOVE_SINGLE, version.get());
	}
}