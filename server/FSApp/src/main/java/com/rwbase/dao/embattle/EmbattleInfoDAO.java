package com.rwbase.dao.embattle;

import com.playerdata.embattle.EmbattleInfo;
import com.rw.fsutil.cacheDao.DataKVDao;

/*
 * @author HC
 * @date 2016年7月15日 下午11:05:49
 * @Description 
 */
public class EmbattleInfoDAO extends DataKVDao<EmbattleInfo> {
	private static EmbattleInfoDAO dao = new EmbattleInfoDAO();

	public static EmbattleInfoDAO getDAO() {
		return dao;
	}

	EmbattleInfoDAO() {
	}
}