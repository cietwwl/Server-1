package com.bm.groupChamp.data;

import com.rw.fsutil.cacheDao.DataRdbDao;


public class GroupChampDataDAO extends DataRdbDao<GroupChampData> {
	
	private static GroupChampDataDAO instance = new GroupChampDataDAO();

	public static GroupChampDataDAO getInstance() {
		return instance;
	}

	private GroupChampDataDAO() {
	}

	public GroupChampData get(String groupId) {
		return getObject(groupId);
	}

	/**
	 * 更新数据
	 * 
	 * @param data
	 */
	public boolean update(GroupChampData data) {
		return saveOrUpdate(data);
	}
}