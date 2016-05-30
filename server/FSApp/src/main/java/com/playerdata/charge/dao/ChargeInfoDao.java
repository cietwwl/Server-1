package com.playerdata.charge.dao;

import com.rw.fsutil.cacheDao.DataKVDao;



public class ChargeInfoDao extends DataKVDao<ChargeInfo>{
	
	private static ChargeInfoDao instance  =  new ChargeInfoDao();
	
	private ChargeInfoDao(){super();};
	
	public static ChargeInfoDao getInstance(){
		return instance;
	}
}
