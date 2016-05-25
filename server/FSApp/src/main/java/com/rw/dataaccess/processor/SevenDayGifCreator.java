package com.rw.dataaccess.processor;

import java.util.ArrayList;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.business.SevenDayGifInfo;

public class SevenDayGifCreator implements DataExtensionCreator<SevenDayGifInfo>{

	@Override
	public SevenDayGifInfo create(String userId) {
		SevenDayGifInfo _table = new SevenDayGifInfo();
		_table.setUserId(userId);
		_table.setCounts(new ArrayList<Integer>());
		_table.setCount(1);
		_table.setLastResetTime(System.currentTimeMillis());
		return _table;
	}

}

