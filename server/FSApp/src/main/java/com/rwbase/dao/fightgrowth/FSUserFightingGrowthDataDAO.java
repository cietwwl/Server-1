package com.rwbase.dao.fightgrowth;

import com.playerdata.fightgrowth.FSUserFightingGrowthData;
import com.rw.fsutil.cacheDao.DataKVDao;

public class FSUserFightingGrowthDataDAO extends DataKVDao<FSUserFightingGrowthData> {

	private static final FSUserFightingGrowthDataDAO _instance = new FSUserFightingGrowthDataDAO();
	
	protected FSUserFightingGrowthDataDAO() {
	}
	
	public static FSUserFightingGrowthDataDAO getInstance() {
		return _instance;
	}
}
