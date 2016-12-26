package com.rwbase.dao.user.accountInfo;

import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class UserMappingDAO extends PFDataRdbDao<UserMappingInfo>{

	
	private static UserMappingDAO instance = new UserMappingDAO();
	
	private UserMappingDAO(){}
	
	public static UserMappingDAO getInstance(){
		return instance;
	}
	
	@SuppressWarnings("deprecation")
	public List<UserMappingInfo> getUserZone(String openAccount){
		return this.findByKey("open_account", openAccount);
	}
}
