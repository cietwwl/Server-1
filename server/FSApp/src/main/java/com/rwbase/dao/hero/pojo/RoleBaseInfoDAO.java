package com.rwbase.dao.hero.pojo;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.manager.GameManager;


public class RoleBaseInfoDAO extends DataKVDao<RoleBaseInfo> {
	private static RoleBaseInfoDAO m_instance = new RoleBaseInfoDAO();
	private RoleBaseInfoDAO(){}
	
	public static RoleBaseInfoDAO getInstance(){
		return m_instance;
	}
	
	public boolean update(RoleBaseInfo t) {
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