package com.rwbase.dao.group.pojo.db.dao;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.rw.fsutil.cacheDao.DataRdbDao;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

/*
 * @author HC
 * @date 2016年1月19日 下午12:11:22
 * @Description 角色的帮派数据
 */
public class UserGroupAttributeDataDAO extends DataRdbDao<UserGroupAttributeData> {
	private static UserGroupAttributeDataDAO dao = new UserGroupAttributeDataDAO();

	public static UserGroupAttributeDataDAO getDAO() {
		return dao;
	}

	protected UserGroupAttributeDataDAO() {
	}

	/**
	 * 获取角色在帮派中的某些属性
	 *
	 * @param userId 用户的Id
	 * @return
	 */
	public UserGroupAttributeData getUserGroupAttributeData(String userId) {
		UserGroupAttributeData data = dao.getObject(userId);
		if (data != null) {
			if (!data.isInit()) {
				synchronized (data) {
					if (!data.isInit()) {
						// 设置其他内存数据
						initMemoryCahce(data);
					}
				}
			}
		}
		return data;
	}

	/**
	 * 初始化内存数据
	 * 
	 * @param userGroupData
	 */
	private void initMemoryCahce(UserGroupAttributeData userGroupData) {
		userGroupData.setInit(true);

		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		// 检查个人成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(userGroupData.getId(), false);
		if (memberData == null) {
			return;
		}

		userGroupData.setGroupName(groupData.getGroupName());
		userGroupData.setDayContribution(memberData.getDayContribution());
		userGroupData.setJoinTime(memberData.getReceiveTime());
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