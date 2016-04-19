package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hero.pojo.TableUserHero;
import com.rwbase.dao.user.User;

public class UserHeroProcessor implements PlayerCreatedProcessor<TableUserHero>{

	@Override
	public TableUserHero create(PlayerCreatedParam param) {
		TableUserHero userHeroTmp = new TableUserHero();
		String userId = param.getUserId();
		userHeroTmp.setUserId(userId);
		userHeroTmp.getHeroIds().add(userId);
		return userHeroTmp;
	}

}
