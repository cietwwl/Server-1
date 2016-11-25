package com.rwbase.dao.battletower.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTowerStrategy;

/*
 * @author HC
 * @date 2015年9月2日 下午5:41:42
 * @Description 试练塔公用的里程碑关卡Dao
 */
public class TableBattleTowerStrategyDao extends DataKVDao<TableBattleTowerStrategy> {
	private static TableBattleTowerStrategyDao dao = new TableBattleTowerStrategyDao();

	/**
	 * 获取试练塔公用里程碑关卡Dao实例
	 * 
	 * @return
	 */
	public static TableBattleTowerStrategyDao getDao() {
		return dao;
	}

	public TableBattleTowerStrategy getStrategy(int groupId) {
		TableBattleTowerStrategy tableBattleTowerStrategy = this.get(String.valueOf(groupId));
		if (tableBattleTowerStrategy == null) {
			tableBattleTowerStrategy = new TableBattleTowerStrategy();
			tableBattleTowerStrategy.setBattleTowerGroupId(groupId);
			this.update(tableBattleTowerStrategy);
		}

		return tableBattleTowerStrategy;
	}
}