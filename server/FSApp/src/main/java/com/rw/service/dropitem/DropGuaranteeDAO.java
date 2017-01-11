package com.rw.service.dropitem;

import com.rw.fsutil.cacheDao.DataKVDao;

public class DropGuaranteeDAO extends DataKVDao<DropGuaranteeData> {

	private static DropGuaranteeDAO instance = new DropGuaranteeDAO();

	public static DropGuaranteeDAO getInstance() {
		return instance;
	}
	
}
