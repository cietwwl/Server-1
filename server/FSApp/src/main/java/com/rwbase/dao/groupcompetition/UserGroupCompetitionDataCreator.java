package com.rwbase.dao.groupcompetition;

import java.util.ArrayList;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionData;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionScoreRecord;

public class UserGroupCompetitionDataCreator implements DataExtensionCreator<UserGroupCompetitionData> {

	@Override
	public UserGroupCompetitionData create(String key) {
		return createData(key);
	}
	
	public static UserGroupCompetitionData createData(String key) {
		UserGroupCompetitionData data = new UserGroupCompetitionData();
		data.setRecords(new ArrayList<UserGroupCompetitionScoreRecord>());
		return data;
	}

}
