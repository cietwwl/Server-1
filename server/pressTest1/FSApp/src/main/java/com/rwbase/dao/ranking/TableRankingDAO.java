package com.rwbase.dao.ranking;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableRankingDAO extends DataKVDao<TableRanking> {
	private static TableRankingDAO m_instance = new TableRankingDAO();
	
	public TableRankingDAO(){
		
	}
	
	public static TableRankingDAO getInstance(){
		return m_instance;
	}
}