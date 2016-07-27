package com.playerdata.teambattle.data;

import com.rw.fsutil.cacheDao.DataKVDao;

public class UserTeamBattleDAO extends DataKVDao<UserTeamBattleData>{
	private static UserTeamBattleDAO instance = new UserTeamBattleDAO();

	public static UserTeamBattleDAO getInstance() {
		return instance;
	}

	private UserTeamBattleDAO() { }
}
