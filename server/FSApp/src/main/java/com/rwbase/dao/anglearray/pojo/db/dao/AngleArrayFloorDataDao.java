package com.rwbase.dao.anglearray.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayFloorData;

/*
 * @author HC
 * @date 2015年11月11日 下午3:03:51
 * @Description 万仙阵层数信息Dao
 */
public class AngleArrayFloorDataDao extends DataKVDao<TableAngleArrayFloorData> {
	private static AngleArrayFloorDataDao instance = new AngleArrayFloorDataDao();

	/**
	 * 获取万仙阵层数数据的Dao数据
	 * 
	 * @return
	 */
	public static AngleArrayFloorDataDao getDao() {
		return instance;
	}

	private AngleArrayFloorDataDao() {
	}
}