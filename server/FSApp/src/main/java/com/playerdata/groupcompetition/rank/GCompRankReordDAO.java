package com.playerdata.groupcompetition.rank;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GCompRankReordDAO extends DataKVDao<GCompRankReordData>{
	private static GCompRankReordDAO instance = new GCompRankReordDAO();

	public static GCompRankReordDAO getInstance() {
		return instance;
	}

	protected GCompRankReordDAO() {
	}
}
