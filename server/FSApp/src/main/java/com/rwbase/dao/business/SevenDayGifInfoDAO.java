package com.rwbase.dao.business;

import com.rw.fsutil.cacheDao.DataKVDao;

public class SevenDayGifInfoDAO extends DataKVDao<SevenDayGifInfo> {
	private static SevenDayGifInfoDAO m_instance = new SevenDayGifInfoDAO();
	
	public SevenDayGifInfoDAO(){
		
	}
	
	public static SevenDayGifInfoDAO getInstance(){
		return m_instance;
	}
}
