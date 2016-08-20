package com.rw.dataaccess.processor;

import com.playerdata.teambattle.data.UserTeamBattleData;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

public class UserTeamBattleDataCreator implements DataExtensionCreator<UserTeamBattleData> {

	@Override
	public UserTeamBattleData create(String userId) {
		UserTeamBattleData utbData = new UserTeamBattleData();
		utbData.setId(userId);
		utbData.dailyReset();
		return utbData;
	}

}
