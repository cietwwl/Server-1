package com.playerdata.embattle;

import java.util.List;

import org.springframework.util.StringUtils;

import com.playerdata.Player;
import com.rwbase.dao.embattle.EmbattleInfoDAO;

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

	// private eSynType synType = eSynType.EmbattleInfo;// 同步数据

	EmbattleInfoMgr() {
	}

	/**
	 * 获取阵容信息
	 * 
	 * @param player
	 * @param type
	 * @param recordKey
	 * @return
	 */
	public synchronized EmbattlePositionInfo getEmbattlePositionInfo(String userId, int type, String recordKey) {
		EmbattleInfo item = get(userId);
		if (item == null) {
			return null;
		}

		recordKey = StringUtils.isEmpty(recordKey) ? "0" : recordKey;

		return item.getEmbattlePositionInfo(type, recordKey);
	}

	/**
	 * 增加或者更新阵容信息
	 * 
	 * @param player
	 * @param type
	 * @param recordKey 当没有特殊的记录Key是填null
	 * @param heroPos
	 */
	public synchronized void updateOrAddEmbattleInfo(Player player, int type, String recordKey, List<EmbattleHeroPosition> heroPos) {
		recordKey = StringUtils.isEmpty(recordKey) ? "0" : recordKey;

		String userId = player.getUserId();
		EmbattleInfo item = get(userId);

		if (item == null) {
			item = new EmbattleInfo(userId);
			item.updateOrAddEmbattleInfo(type, recordKey, heroPos);
			syn(player, type, true);
		} else {
			item.updateOrAddEmbattleInfo(type, recordKey, heroPos);
			syn(player, type, false);
		}

		EmbattleInfoDAO.getDAO().update(item);
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
		EmbattleInfo item = get(userId);
		if (item == null) {
			return;
		}

		item.removeEmbattleInfo(type, recordKey);
		EmbattleInfoDAO.getDAO().update(item);

		syn(player, type, false);
	}

	/**
	 * 同步列表数据
	 * 
	 * @param player
	 */
	public void synAll(Player player) {
		// String userId = player.getUserId();
		// MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		// Enumeration<EmbattleInfo> enumeration = mapItemStore.getEnum();
		//
		// List<EmbattleInfo> list = new ArrayList<EmbattleInfo>();
		// while (enumeration.hasMoreElements()) {
		// list.add(enumeration.nextElement());
		// }
		//
		// ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 同步单条数据
	 * 
	 * @param player
	 * @param key
	 */
	public void syn(Player player, int key, boolean isAdd) {
		// String userId = player.getUserId();
		// MapItemStore<EmbattleInfo> mapItemStore = get(userId);
		// String pKey = String.valueOf(key);
		// EmbattleInfo item = mapItemStore.getItem(pKey);
		// if (item == null) {
		// return;
		// }
		//
		// ClientDataSynMgr.synData(player, item, synType, isAdd ? eSynOpType.ADD_SINGLE : eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 获取缓存数据
	 * 
	 * @param userId
	 * @return
	 */
	private EmbattleInfo get(String userId) {
		return EmbattleInfoDAO.getDAO().get(userId);
	}
}