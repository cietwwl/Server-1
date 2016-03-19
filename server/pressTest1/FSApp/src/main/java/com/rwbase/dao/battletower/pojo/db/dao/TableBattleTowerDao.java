package com.rwbase.dao.battletower.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;

/*
 * @author HC
 * @date 2015年9月1日 下午12:03:56
 * @Description 试练塔的数据Dao
 */
public class TableBattleTowerDao extends DataKVDao<TableBattleTower> {
	private static TableBattleTowerDao dao;

	/**
	 * 获取试练塔数据库的Dao
	 */
	public static TableBattleTowerDao getDao() {
		if (dao == null) {
			dao = new TableBattleTowerDao();
		}
		return dao;
	}
}