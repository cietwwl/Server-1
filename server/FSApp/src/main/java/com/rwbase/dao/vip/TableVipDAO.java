package com.rwbase.dao.vip;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.vip.pojo.TableVip;

public class TableVipDAO extends DataKVDao<TableVip> {
	protected TableVipDAO(){}
	private static TableVipDAO instance = new TableVipDAO();
	public static TableVipDAO getInstance(){
		return instance;
	}	
}
