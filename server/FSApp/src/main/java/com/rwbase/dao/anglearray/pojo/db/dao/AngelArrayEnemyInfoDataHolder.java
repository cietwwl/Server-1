package com.rwbase.dao.anglearray.pojo.db.dao;

import java.util.List;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayEnemyInfoData;

/*
 * @author HC
 * @date 2016年4月20日 下午9:14:09
 * @Description 万仙阵敌方佣兵对应信息
 */
public class AngelArrayEnemyInfoDataHolder {

	private final String userId;

	public AngelArrayEnemyInfoDataHolder(String userId) {
		this.userId = userId;
	}

	private MapItemStore<AngelArrayEnemyInfoData> getMapItemStore() {
		MapItemStoreCache<AngelArrayEnemyInfoData> angelArrayEnemyInfoData = MapItemStoreFactory.getAngelArrayEnemyInfoData();
		return angelArrayEnemyInfoData.getMapItemStore(userId, AngelArrayEnemyInfoData.class);
	}

	/**
	 * 增加数据
	 * 
	 * @param enemyInfo
	 */
	public void addAngelArrayEnemyInfoData(AngelArrayEnemyInfoData enemyInfo) {
		getMapItemStore().addItem(enemyInfo);
	}

	/**
	 * 获取敌人的血量信息
	 * 
	 * @param id
	 * @return
	 */
	public AngelArrayEnemyInfoData getAngelArrayEnemyInfoData(String id) {
		return getMapItemStore().getItem(id);
	}

	/**
	 * 重置所有层记录的敌人血量信息
	 */
	public void resetAllAngelArrayEnemyInfoData() {
		MapItemStore<AngelArrayEnemyInfoData> mapItemStore = getMapItemStore();
		List<String> readOnlyKeyList = mapItemStore.getReadOnlyKeyList();
		for (int i = 0, size = readOnlyKeyList.size(); i < size; i++) {
			mapItemStore.removeItem(readOnlyKeyList.get(i));
		}
	}

	/**
	 * 刷新数据到数据库
	 */
	public void flush() {
		getMapItemStore().flush();
	}
}