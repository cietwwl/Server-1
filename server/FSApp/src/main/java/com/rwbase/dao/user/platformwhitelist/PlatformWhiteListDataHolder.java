package com.rwbase.dao.user.platformwhitelist;

import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class PlatformWhiteListDataHolder extends PFDataRdbDao<PlatformWhiteList>{
	
	
	private static PlatformWhiteListDataHolder instance = new PlatformWhiteListDataHolder();
	
	public static PlatformWhiteListDataHolder getInstance() {
		if (instance == null) {
			instance = new PlatformWhiteListDataHolder();
		}
		return instance;
	}
	
	public List<PlatformWhiteList> getAllWhiteList(){
		return super.getAll();
	}
}

