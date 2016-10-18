package com.rwbase.dao.hero;

import com.playerdata.hero.core.FSUserHeroGlobalDataMgr;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.hero.pojo.FSUserHeroGlobalData;

public class FSUserHeroGlobalDataDAO extends DataKVDao<FSUserHeroGlobalData> {

	private static final FSUserHeroGlobalDataDAO _INSTANCE = new FSUserHeroGlobalDataDAO();

	protected FSUserHeroGlobalDataDAO() {
	}

	public static FSUserHeroGlobalDataDAO getInstance() {
		return _INSTANCE;
	}

	@Override
	public boolean update(FSUserHeroGlobalData t) {
		boolean update = super.update(t);
		if (update) {// 同步数据到前台
			FSUserHeroGlobalDataMgr.getInstance().synData(t.getUserId());
		}
		return update;
	}
}