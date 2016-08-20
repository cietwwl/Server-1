package com.rwbase.dao.angelarray.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.angelarray.pojo.db.TableAngelArrayData;

/*
 * @author HC
 * @date 2015年11月11日 下午3:03:33
 * @Description 万仙阵个人数据Dao
 */
public class AngelArrayDataDao extends DataRdbDao<TableAngelArrayData> {

	private static AngelArrayDataDao instance = new AngelArrayDataDao();

	/**
	 * 获取个人数据Dao
	 * 
	 * @return
	 */
	public static AngelArrayDataDao getDao() {
		return instance;
	}

	private AngelArrayDataDao() {
	}

	/**
	 * 通过Key获取数据
	 *
	 * @param key
	 * @return
	 */
	public TableAngelArrayData getAngleArrayDataByKey(String key) {
		return this.getObject(key);
	}

	/**
	 * 保存或者更新数据
	 *
	 * @param data
	 * @return
	 */
	public boolean addOrUpdateAngleArrayData(TableAngelArrayData data) {
		return this.saveOrUpdate(data);
	}
}