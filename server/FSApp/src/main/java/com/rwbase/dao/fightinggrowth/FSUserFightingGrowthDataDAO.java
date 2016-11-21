package com.rwbase.dao.fightinggrowth;

import com.playerdata.fightinggrowth.FSUserFightingGrowthData;
import com.rw.fsutil.cacheDao.DataKVDao;

public class FSUserFightingGrowthDataDAO extends DataKVDao<FSUserFightingGrowthData> {

	private static FSUserFightingGrowthDataDAO _instance = new FSUserFightingGrowthDataDAO();
	
	protected FSUserFightingGrowthDataDAO() {
	}
	
	public static FSUserFightingGrowthDataDAO getInstance() {
		return _instance;
	}
}
