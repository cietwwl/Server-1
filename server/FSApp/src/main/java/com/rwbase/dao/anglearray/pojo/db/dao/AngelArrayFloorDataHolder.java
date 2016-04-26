package com.rwbase.dao.anglearray.pojo.db.dao;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.team.TeamInfo;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayFloorData;

/*
 * @author HC
 * @date 2016年4月20日 下午4:43:02
 * @Description 万仙阵个人的层数信息记录
 */
public class AngelArrayFloorDataHolder {

	private final String userId;// 查询此表的主Key

	public AngelArrayFloorDataHolder(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取MapItemStore
	 * 
	 * @return
	 */
	private MapItemStore<AngelArrayFloorData> getMapItemStore() {
		MapItemStoreCache<AngelArrayFloorData> angelArrayFloorData = MapItemStoreFactory.getAngelArrayFloorData();
		return angelArrayFloorData.getMapItemStore(userId, AngelArrayFloorData.class);
	}

	/**
	 * 获取层的数据
	 * 
	 * @param id
	 * @return
	 */
	public AngelArrayFloorData getAngelArrayFloorData(String id) {
		return getMapItemStore().getItem(id);
	}

	/**
	 * 增加层信息
	 * 
	 * @param floorData
	 */
	public void addAngelArrayFloorData(AngelArrayFloorData floorData) {
		if (floorData == null) {
			return;
		}

		getMapItemStore().addItem(floorData);
	}

	/**
	 * 获取已经产生的敌人的信息
	 * 
	 * @return
	 */
	public List<String> getEnemyUserIdList() {
		List<String> userIdList = new ArrayList<String>();

		MapItemStore<AngelArrayFloorData> mapItemStore = getMapItemStore();
		Enumeration<AngelArrayFloorData> values = mapItemStore.getEnum();
		while (values.hasMoreElements()) {
			AngelArrayFloorData value = values.nextElement();
			if (value == null) {
				continue;
			}

			TeamInfo teamInfo = value.getTeamInfo();
			if (teamInfo == null) {
				continue;
			}

			userIdList.add(teamInfo.getUuid());
		}

		return userIdList;
	}

	/**
	 * 获取只读的Key列表
	 * 
	 * @return
	 */
	public List<String> getReadOnlyKeyList() {
		return getMapItemStore().getReadOnlyKeyList();
	}

	/**
	 * 清除所有的万仙阵层信息
	 */
	public void resetAllAngelArrayFloorData() {
		MapItemStore<AngelArrayFloorData> mapItemStore = getMapItemStore();

		List<String> readOnlyKeyList = mapItemStore.getReadOnlyKeyList();
		for (int i = 0, size = readOnlyKeyList.size(); i < size; i++) {
			mapItemStore.removeItem(readOnlyKeyList.get(i));
		}
	}
}