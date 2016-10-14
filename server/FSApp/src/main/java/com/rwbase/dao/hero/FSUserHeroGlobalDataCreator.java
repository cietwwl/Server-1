package com.rwbase.dao.hero;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.hero.pojo.FSUserHeroGlobalData;

public class FSUserHeroGlobalDataCreator implements DataExtensionCreator<FSUserHeroGlobalData> {

	@Override
	public FSUserHeroGlobalData create(String key) {
		return new FSUserHeroGlobalData(key);
	}

}
