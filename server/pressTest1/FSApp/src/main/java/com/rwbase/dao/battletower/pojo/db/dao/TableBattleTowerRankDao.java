package com.rwbase.dao.battletower.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTowerRank;

/*
 * @author HC
 * @date 2015年9月3日 下午4:08:27
 * @Description 试练塔个人历史最高层数的详细信息
 */
public class TableBattleTowerRankDao extends DataKVDao<TableBattleTowerRank> {
	private static TableBattleTowerRankDao dao = new TableBattleTowerRankDao();// 不开放的单例

	// /**
	// * 获取数据
	// *
	// * @param userId
	// * @return
	// */
	// public static TableBattleTowerRank getRankByKey(String dbkey) {
	// return dao.getObject(dbkey);
	// }

	/**
	 * 获取数据
	 * 
	 * @param userId
	 * @return
	 */
	public static TableBattleTowerRank getRankByKey(String userId) {
		// return dao.getObject(userId);
		return dao.get(userId);
	}

	/**
	 * 增加或者更新数据
	 *
	 * @param rank
	 */
	public static void updateValue(TableBattleTowerRank rank) {
		dao.update(rank);
	}

	// /**
	// * 增加或者更新数据
	// *
	// * @param rank
	// */
	// public static void update(TableBattleTowerRank rank) {
	// dao.saveOrUpdate(rank);
	// }
}