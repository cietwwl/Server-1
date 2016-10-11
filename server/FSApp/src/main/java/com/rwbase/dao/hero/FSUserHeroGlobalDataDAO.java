package com.rwbase.dao.hero;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.hero.pojo.FSUserHeroGlobalData;

public class FSUserHeroGlobalDataDAO extends DataKVDao<FSUserHeroGlobalData> {
	
	private static final FSUserHeroGlobalDataDAO _INSTANCE = new FSUserHeroGlobalDataDAO();
	
	protected FSUserHeroGlobalDataDAO() {}
	
	public static FSUserHeroGlobalDataDAO getInstance() {
		return _INSTANCE;
	}
}
