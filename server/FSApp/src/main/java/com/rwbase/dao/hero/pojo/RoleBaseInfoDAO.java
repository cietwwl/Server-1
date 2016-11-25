package com.rwbase.dao.hero.pojo;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.manager.GameManager;


public class RoleBaseInfoDAO extends DataKVDao<RoleBaseInfoIF> {
	private static RoleBaseInfoDAO m_instance = new RoleBaseInfoDAO();
	protected RoleBaseInfoDAO(){}
	
	public static RoleBaseInfoDAO getInstance(){
		return m_instance;
	}
	
	public boolean update(RoleBaseInfoIF t) {
		return super.update(t);
	}
	
	/**
	 * 获取缓存数量大小
	 * @return
	 */
	protected int getCacheSize(){
		return GameManager.getPerformanceConfig().getHeroCapacity();
	}
}