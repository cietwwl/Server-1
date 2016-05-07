package com.rwbase.dao.upgrade.pojo;

import com.rw.fsutil.cacheDao.DataKVCacheDao;

public class TableUpgradeDao extends DataKVCacheDao<TableUpgradeData>{
	private static TableUpgradeDao m_instance = new TableUpgradeDao();
	private TableUpgradeDao(){}
	public static TableUpgradeDao getInstance(){
		return m_instance;
	}
}
