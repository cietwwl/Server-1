package com.rw.dataaccess.processor;

import com.playerdata.fightinggrowth.FSUserFightingGrowthData;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class FSUserFightingGrowthDataCreator implements DataExtensionCreator<FSUserFightingGrowthData> {

	@Override
	public FSUserFightingGrowthData create(String key) {
		FSUserFightingGrowthData data = new FSUserFightingGrowthData();
		data.setUserId(key);
		return data;
	}

}
