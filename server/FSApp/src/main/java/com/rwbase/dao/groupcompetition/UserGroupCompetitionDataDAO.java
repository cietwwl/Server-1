package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionData;

public class UserGroupCompetitionDataDAO extends DataKVDao<UserGroupCompetitionData> {

	private static UserGroupCompetitionDataDAO _instance = new UserGroupCompetitionDataDAO();
	
	public static UserGroupCompetitionDataDAO getInstance() {
		return _instance;
	}
}
