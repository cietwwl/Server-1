package com.rwbase.dao.praise.db;

import com.rw.fsutil.cacheDao.DataKVDao;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:12:38
 * @desc 点赞的DAO
 **/

public class PraiseDAO extends DataKVDao<PraiseData> {
	private static PraiseDAO dao = new PraiseDAO();

	public static PraiseDAO getDAO() {
		return dao;
	}

	private PraiseDAO() {
	}
}