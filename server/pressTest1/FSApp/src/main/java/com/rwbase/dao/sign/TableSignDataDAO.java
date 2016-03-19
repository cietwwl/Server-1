package com.rwbase.dao.sign;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.sign.pojo.TableSignData;

public class TableSignDataDAO extends DataKVDao<TableSignData>
{
	private static TableSignDataDAO instance = new TableSignDataDAO();
	private TableSignDataDAO(){}
	
	public static TableSignDataDAO getInstance()
	{
		return instance;
	}
}
