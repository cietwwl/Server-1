package com.rwbase.dao.guide;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.guide.pojo.TableGuide;

public class TableGuideDAO extends DataKVDao<TableGuide> {

	private static TableGuideDAO instance = new TableGuideDAO();
	public static TableGuideDAO getInstance(){
		return instance;
	}
}
