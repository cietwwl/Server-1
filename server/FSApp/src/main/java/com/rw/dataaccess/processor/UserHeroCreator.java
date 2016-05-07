package com.rw.dataaccess.processor;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.hero.pojo.TableUserHero;

public class UserHeroCreator implements DataExtensionCreator<TableUserHero>{

	@Override
	public TableUserHero create(String userId) {
		TableUserHero userHeroTmp = new TableUserHero();
		userHeroTmp.setUserId(userId);
		userHeroTmp.addHeroId(userId);
		return userHeroTmp;
	}

}
