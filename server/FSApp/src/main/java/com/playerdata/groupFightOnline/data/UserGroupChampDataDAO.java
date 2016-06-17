package com.playerdata.groupFightOnline.data;

import com.rw.fsutil.cacheDao.DataRdbDao;


public class UserGroupChampDataDAO extends DataRdbDao<UserGroupChampData> {
	
	private static UserGroupChampDataDAO instance = new UserGroupChampDataDAO();

	public static UserGroupChampDataDAO getInstance() {
		return instance;
	}

	public UserGroupChampData get(String groupId) {
		return getObject(groupId);
	}

	/**
	 * 更新数据
	 * 
	 * @param data
	 */
	public boolean update(UserGroupChampData data) {
		return saveOrUpdate(data);
	}
}