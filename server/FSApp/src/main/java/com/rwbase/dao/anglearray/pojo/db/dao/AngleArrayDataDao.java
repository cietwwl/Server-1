package com.rwbase.dao.anglearray.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayData;

/*
 * @author HC
 * @date 2015年11月11日 下午3:03:33
 * @Description 万仙阵个人数据Dao
 */
public class AngleArrayDataDao extends DataRdbDao<TableAngleArrayData> {

	private static AngleArrayDataDao instance = new AngleArrayDataDao();

	/**
	 * 获取个人数据Dao
	 * 
	 * @return
	 */
	public static AngleArrayDataDao getDao() {
		return instance;
	}

	private AngleArrayDataDao() {
	}

	/**
	 * 通过Key获取数据
	 *
	 * @param key
	 * @return
	 */
	public TableAngleArrayData getAngleArrayDataByKey(String key) {
		return this.getObject(key);
	}

	/**
	 * 保存或者更新数据
	 *
	 * @param data
	 * @return
	 */
	public boolean addOrUpdateAngleArrayData(TableAngleArrayData data) {
		return this.saveOrUpdate(data);
	}
}