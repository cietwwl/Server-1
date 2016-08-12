package com.rwbase.dao.email;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableEmailDAO  extends DataKVDao<TableEmail> {
	private static TableEmailDAO m_instance = new TableEmailDAO();
	
	public static TableEmailDAO getInstance(){
		return m_instance;
	}
	
	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	@Override
	protected int getUpdatedSeconds() {
		return 300;
	}
}
