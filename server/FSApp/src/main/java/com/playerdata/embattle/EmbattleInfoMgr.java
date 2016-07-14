package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年7月14日 下午7:21:50
 * @Description 
 */
public class EmbattleInfoMgr {
	private static EmbattleInfoMgr mgr = new EmbattleInfoMgr();

	public static EmbattleInfoMgr getMgr() {
		return mgr;
	}

	private eSynType synType = eSynType.EmbattleInfo;// 同步数据

	EmbattleInfoMgr() {
	}

	/**
	 * 增加或者更新阵容信息
	 * 
	 * @param player
	 * @param type
	 * @param recoreKey
	 * @param heroPos
	 */
	public void updateOrAddEmbattleInfo(Player player, int key, String recoreKey, List<EmbattleHeroPosition> heroPos) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		String pKey = String.valueOf(key);
		EmbattleInfo item = mapItemStore.getItem(pKey);
		if (item == null) {
			item = new EmbattleInfo(userId, key);
			item.updateOrAddEmbattleInfo(recoreKey, heroPos);
			mapItemStore.addItem(item);
			syn(player, key, true);
		} else {
			item.updateOrAddEmbattleInfo(recoreKey, heroPos);
			mapItemStore.update(pKey);
			syn(player, key, false);
		}
	}

	/**
	 * 删除某个类型中的阵容记录
	 * 
	 * @param player
	 * @param key
	 * @param recoreKey
	 */
	public void removeEmbattleInfo(Player player, int key, String recoreKey) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		String pKey = String.valueOf(key);
		EmbattleInfo item = mapItemStore.getItem(pKey);
		if (item == null) {
			return;
		}

		item.removeEmbattleInfo(recoreKey);
		mapItemStore.update(pKey);

		syn(player, key, false);
	}

	/**
	 * 同步列表数据
	 * 
	 * @param player
	 */
	public void synAll(Player player) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		Enumeration<EmbattleInfo> enumeration = mapItemStore.getEnum();

		List<EmbattleInfo> list = new ArrayList<EmbattleInfo>();
		while (enumeration.hasMoreElements()) {
			list.add(enumeration.nextElement());
		}

		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 同步单条数据
	 * 
	 * @param player
	 * @param key
	 */
	public void syn(Player player, int key, boolean isAdd) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		String pKey = String.valueOf(key);
		EmbattleInfo item = mapItemStore.getItem(pKey);
		if (item == null) {
			return;
		}

		ClientDataSynMgr.synData(player, item, synType, isAdd ? eSynOpType.ADD_SINGLE : eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 获取缓存数据
	 * 
	 * @param userId
	 * @return
	 */
	private MapItemStore<EmbattleInfo> get(String userId) {
		MapItemStore<EmbattleInfo> mapItemStore = MapItemStoreFactory.getEmbattleInfoCache().getMapItemStore(userId, EmbattleInfo.class);
		return mapItemStore;
	}
}