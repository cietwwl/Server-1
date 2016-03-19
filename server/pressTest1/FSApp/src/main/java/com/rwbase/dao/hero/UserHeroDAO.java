package com.rwbase.dao.hero;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.hero.pojo.TableUserHero;

public class UserHeroDAO extends DataKVDao<TableUserHero> {
	private static UserHeroDAO instance = new UserHeroDAO();
	private UserHeroDAO(){}
	public static UserHeroDAO getInstance(){
		return instance;
	}

}
