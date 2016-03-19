package com.rwbase.dao.group.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.group.pojo.db.GroupBaseData;

/*
 * @author HC
 * @date 2016年1月18日 下午2:39:47
 * @Description 帮派基础数据管理DAO
 */
public class GroupBaseDataDAO extends DataRdbDao<GroupBaseData> {
	private static GroupBaseDataDAO dao = new GroupBaseDataDAO();

	public static GroupBaseDataDAO getDAO() {
		return dao;
	}

	private GroupBaseDataDAO() {
	}

	/**
	 * 获取帮派的基础数据
	 *
	 * @param groupId 帮派的Id
	 * @return
	 */
	public GroupBaseData getGroupData(String groupId) {
		return dao.getObject(groupId);
	}

	/**
	 * 更新数据
	 * 
	 * @param data
	 */
	public boolean update(GroupBaseData data) {
		return dao.saveOrUpdate(data);
	}
}