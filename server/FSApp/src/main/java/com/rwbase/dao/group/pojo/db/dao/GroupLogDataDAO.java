package com.rwbase.dao.group.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataKVCacheDao;
import com.rwbase.dao.group.pojo.db.GroupLogData;

/*
 * @author HC
 * @date 2016年1月31日 上午11:59:12
 * @Description 帮派日志的DAO
 */
public class GroupLogDataDAO extends DataKVCacheDao<GroupLogData> {

	private static GroupLogDataDAO dao;

	public static GroupLogDataDAO getDAO() {
		if (dao == null) {
			dao = new GroupLogDataDAO();
		}
		return dao;
	}

	private GroupLogDataDAO() {
	}

	/**
	 * 获取帮派日志数据
	 * 
	 * @param groupId
	 * @return
	 */
	public GroupLogData getLogData(String groupId) {
		GroupLogData groupLogData = dao.get(groupId);
		if (groupLogData == null) {
			groupLogData = new GroupLogData(groupId);
			dao.add(groupLogData);
		}

		return groupLogData;
	}
}