package com.rwbase.dao.angelarray.pojo.db.dao;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayTeamInfoData;

/*
 * @author HC
 * @date 2016年4月19日 下午8:50:51
 * @Description 阵容信息
 */
public class AngelArrayTeamInfoDataHolder {
	private static AngelArrayTeamInfoDataHolder holder = new AngelArrayTeamInfoDataHolder();

	/**
	 * 获取万仙阵阵容信息Holder
	 * 
	 * @return
	 */
	public static AngelArrayTeamInfoDataHolder getHolder() {
		return holder;
	}

	/**
	 * 获取MapItemStore
	 * 
	 * @return
	 */
	private MapItemStore<AngelArrayTeamInfoData> getMapItemStore() {
		MapItemStoreCache<AngelArrayTeamInfoData> angelArrayTeamInfoData = MapItemStoreFactory.getAngelArrayTeamInfoData();
		return angelArrayTeamInfoData.getMapItemStore("1", AngelArrayTeamInfoData.class);
	}

	/**
	 * 获取某个战力区间是否已经随机出来了人
	 * 
	 * @param minFighting
	 * @param maxFighting
	 * @param hasEnemyList 要过滤掉那些不刷出来
	 * @return
	 */
	public synchronized AngelArrayTeamInfoData getAngelArrayTeamInfo(int minFighting, int maxFighting, int floor, List<String> hasEnemyList) {
		Enumeration<AngelArrayTeamInfoData> infoMap = getMapItemStore().getEnum();

		AngelArrayTeamInfoData teamInfo = null;
		while (infoMap.hasMoreElements()) {
			AngelArrayTeamInfoData nextElement = infoMap.nextElement();
			if (nextElement == null) {
				continue;
			}

			int minFloor = nextElement.getMinFloor();// 随机时机器人可以出现的最低层
			int maxFloor = nextElement.getMaxFloor();// 随机时机器人可以出现的最高层
			if (minFloor <= 0 && maxFloor > 0 && floor > maxFloor) {// 随机小层是<=0，意思是不限制出现的低层
				continue;
			}

			if (maxFloor <= 0 && floor < minFloor) {// 随机上层是<=0，意思是不限制出现的高层
				continue;
			}

			if (floor < minFloor || (maxFloor > 0 && floor > maxFloor)) {// 完美避开区间，直接没了任何意义
				continue;
			}

			if (hasEnemyList != null && hasEnemyList.contains(nextElement.getId())) {
				continue;
			}

			int minFightingLimit = nextElement.getMinFighting();// 最小战力
			int maxFightingLimit = nextElement.getMaxFighting();// 最大战力

			if (minFighting >= minFightingLimit && minFighting <= maxFightingLimit) {
				teamInfo = nextElement;
				break;
			}

			if (maxFighting >= minFightingLimit && maxFighting <= maxFightingLimit) {
				teamInfo = nextElement;
				break;
			}
		}

		return teamInfo;
	}

	/**
	 * 增加阵容信息到数据库
	 * 
	 * @param teamInfo
	 */
	public synchronized void addAngelArrayTeamInfo(AngelArrayTeamInfoData teamInfo) {
		if (teamInfo == null) {
			return;
		}

		getMapItemStore().addItem(teamInfo);
	}

	/**
	 * 重置阵容信息数据
	 */
	public synchronized void resetAngelArrayTeamInfo() {
		MapItemStore<AngelArrayTeamInfoData> mapItemStore = getMapItemStore();
		Enumeration<AngelArrayTeamInfoData> infoMap = mapItemStore.getEnum();

		List<String> idList = new ArrayList<String>();

		while (infoMap.hasMoreElements()) {
			AngelArrayTeamInfoData nextElement = infoMap.nextElement();
			if (nextElement == null) {
				continue;
			}

			idList.add(nextElement.getId());
		}

		for (int i = 0, size = idList.size(); i < size; i++) {
			mapItemStore.removeItem(idList.get(i));
		}
	}

	/**
	 * 获取一个只可读的Id列表
	 * 
	 * @return
	 */
	public synchronized List<String> getAllUserIdList() {
		return new ArrayList<String>(getMapItemStore().getReadOnlyKeyList());
	}
}