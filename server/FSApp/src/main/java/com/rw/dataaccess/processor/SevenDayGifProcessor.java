package com.rw.dataaccess.processor;

import java.util.ArrayList;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.business.SevenDayGifInfo;

public class SevenDayGifProcessor implements PlayerCreatedProcessor<SevenDayGifInfo>{

	@Override
	public SevenDayGifInfo create(PlayerCreatedParam param) {
		SevenDayGifInfo _table = new SevenDayGifInfo();
		_table.setUserId(param.getUserId());
		_table.setCount(0);
		_table.setCounts(new ArrayList<Integer>());
		_table.setLastResetTime(0);
		return _table;
	}

}

