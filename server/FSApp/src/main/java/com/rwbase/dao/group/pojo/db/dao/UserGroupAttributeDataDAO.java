package com.rwbase.dao.group.pojo.db.dao;

import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;

/*
 * @author HC
 * @date 2016年1月19日 下午12:11:22
 * @Description 角色的帮派数据
 */
public final class UserGroupAttributeDataDAO extends DataRdbDao<UserGroupAttributeData> {
	private static UserGroupAttributeDataDAO dao = new UserGroupAttributeDataDAO();

	public static UserGroupAttributeDataDAO getDAO() {
		return dao;
	}

	private UserGroupAttributeDataDAO() {
	}

	/**
	 * 获取角色在帮派中的某些属性
	 *
	 * @param userId 用户的Id
	 * @return
	 */
	public UserGroupAttributeData getUserGroupAttributeData(String userId) {
//		UserGroupAttributeData data = dao.getObject(userId);
//		if (data == null) {
//			data = new UserGroupAttributeData();
//			data.setUserId(userId);
//			data.setGroupId("");
//			dao.saveOrUpdate(data);
//		}
//
//		return data;
		return dao.getObject(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param data
	 */
	public boolean update(UserGroupAttributeData data) {
		return dao.saveOrUpdate(data);
	}
}