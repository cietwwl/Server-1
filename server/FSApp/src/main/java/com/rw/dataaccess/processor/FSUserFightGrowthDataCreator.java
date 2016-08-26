package com.rw.dataaccess.processor;

import com.playerdata.fightgrowth.FSUserFightingGrowthData;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class FSUserFightGrowthDataCreator implements DataExtensionCreator<FSUserFightingGrowthData> {

	@Override
	public FSUserFightingGrowthData create(String key) {
		FSUserFightingGrowthData data = new FSUserFightingGrowthData();
		data.setUserId(key);
		return data;
	}

}
