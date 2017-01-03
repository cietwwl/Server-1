package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.util.StringUtils;

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

	protected EmbattleInfoMgr() {
	}

	/**
	 * 获取阵容信息
	 * 
	 * @param player
	 * @param type
	 * @param recordKey 1-4为规范的从其他对应功能获得key值得，5之后为fisher随便写的，比如'copy'
	 * @return
	 */
	public synchronized EmbattlePositionInfo getEmbattlePositionInfo(String userId, int type, String recordKey) {
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		EmbattleInfo item = mapItemStore.getItem(userId + "_" + type);
		if (item == null) {
			return null;
		}

		recordKey = StringUtils.isEmpty(recordKey) ? "0" : recordKey;
		return item.getEmbattlePositionInfo(recordKey);
	}
	
	/**
	 * 
	 * 获取某个类型的阵容的所有英雄
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	public synchronized List<EmbattlePositionInfo> getAllEmbattlePositionInfo(String userId, int type) {
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		EmbattleInfo item = mapItemStore.getItem(userId + "_" + type);
		if (item == null) {
			return Collections.emptyList();
		}
		return item.getAll();
	}

	/**
	 * 增加或者更新阵容信息
	 * 
	 * @param player
	 * @param type
	 * @param recordKey 当没有特殊的记录Key是填null
	 * @param heroPos
	 */
	public synchronized boolean updateOrAddEmbattleInfo(Player player, int type, String recordKey, List<EmbattleHeroPosition> heroPos) {
		// if(heroPos.isEmpty()) {
		// return false;
		// }
		recordKey = StringUtils.isEmpty(recordKey) ? "0" : recordKey;

		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		EmbattleInfo item = mapItemStore.getItem(userId + "_" + type);

		if (item == null) {
			item = new EmbattleInfo(userId, type);
			item.updateOrAddEmbattleInfo(recordKey, heroPos);
			mapItemStore.addItem(item);
			// 推送
			addSyn(player, type);
		} else {
			item.updateOrAddEmbattleInfo(recordKey, heroPos);
			mapItemStore.updateItem(item);
			// 推送
			updateSyn(player, type);
		}
		return true;
	}

	/**
	 * 删除某个类型中的阵容记录
	 * 
	 * @param player
	 * @param type
	 * @param recordKey
	 */
	public synchronized void removeEmbattleInfo(Player player, int type, String recordKey) {

		recordKey = StringUtils.isEmpty(recordKey) ? "0" : recordKey;

		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		EmbattleInfo item = mapItemStore.getItem(userId + "_" + type);
		if (item == null) {
			return;
		}

		item.removeEmbattleInfo(recordKey);
		mapItemStore.updateItem(item);

		// 推送
		deleteSyn(player, type);
	}

	/**
	 * 更新数据
	 * 
	 * @param player
	 */
	public void syn(Player player) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		Enumeration<EmbattleInfo> valueEnum = mapItemStore.getEnum();

		List<EmbattleInfo> list = new ArrayList<EmbattleInfo>();
		while (valueEnum.hasMoreElements()) {
			EmbattleInfo nextElement = valueEnum.nextElement();
			list.add(nextElement);
		}

		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 增加数据
	 * 
	 * @param player
	 * @param type
	 */
	public void addSyn(Player player, int type) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		EmbattleInfo item = mapItemStore.getItem(userId + "_" + type);
		if (item == null) {
			return;
		}

		ClientDataSynMgr.synData(player, item, synType, eSynOpType.ADD_SINGLE);
	}

	/**
	 * 更新数据
	 * 
	 * @param player
	 * @param type
	 */
	public void updateSyn(Player player, int type) {
		String userId = player.getUserId();
		MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		EmbattleInfo item = mapItemStore.getItem(userId + "_" + type);
		if (item == null) {
			return;
		}

		ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 删除数据
	 * 
	 * @param player
	 * @param type
	 */
	public void deleteSyn(Player player, int type) {
		EmbattleInfo info = new EmbattleInfo(player.getUserId(), type);
		ClientDataSynMgr.synData(player, info, synType, eSynOpType.REMOVE_SINGLE);
	}

	/**
	 * 获取缓存数据
	 * 
	 * @param userId
	 * @return
	 */
	private MapItemStore<EmbattleInfo> get(String userId) {
		return MapItemStoreFactory.getEmbattleInfoCache().getMapItemStore(userId, EmbattleInfo.class);
	}
}