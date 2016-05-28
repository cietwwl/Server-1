package com.rwbase.dao.groupsecret.pojo.db;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/*
 * @author HC
 * @date 2016年5月27日 下午7:15:43
 * @Description 角色创建的秘境信息
 */
public class UserCreateGroupSecretData implements IMapItem {
	private String userId;// 创建秘境的角色Id
	private List<GroupSecretData> createList;// 创建的秘境Id

	public UserCreateGroupSecretData() {
		createList = new ArrayList<GroupSecretData>();
	}

	// ////////////////////////////////////////////////逻辑Get区
	@JsonIgnore
	@Override
	public String getId() {
		return userId;
	}

	public List<GroupSecretData> getCreateList() {
		return createList;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setCreateList(List<GroupSecretData> createList) {
		this.createList = createList;
	}

	// ////////////////////////////////////////////////逻辑区
	/**
	 * 获取未被占用的秘境Id
	 * 
	 * @return
	 */
	@JsonIgnore
	private int getUniqueId() {
		if (createList.isEmpty()) {
			return 0;
		}

		int maxId = -1;
		int size = createList.size();
		List<Integer> idList = new ArrayList<Integer>(size);
		for (int i = size - 1; i >= 0; --i) {
			GroupSecretData groupSecretData = createList.get(i);
			int dbId = groupSecretData.getId();
			idList.add(dbId);
			if (dbId > maxId) {
				maxId = dbId;
			}
		}

		int unuseIndex = -1;
		for (int i = 0; i <= maxId; i++) {
			if (!idList.contains(i)) {
				unuseIndex = i;
				break;
			}
		}

		if (unuseIndex == -1) {
			unuseIndex = maxId + 1;
		}
		return unuseIndex;
	}

	/**
	 * 获取秘境的数据
	 * 
	 * @param index
	 * @return
	 */
	@JsonIgnore
	public GroupSecretData getGroupSecretData(int id) {
		if (createList.isEmpty()) {
			return null;
		}

		for (int i = createList.size() - 1; i >= 0; --i) {
			GroupSecretData groupSecretData = createList.get(i);
			if (groupSecretData.getId() == id) {
				return groupSecretData;
			}
		}

		return null;
	}

	/**
	 * 增加秘境的数据
	 * 
	 * @param data
	 */
	public void addGroupSecretData(GroupSecretData data) {
		int newId = getUniqueId();
		data.setId(newId);
		createList.add(data);
	}

	/**
	 * 移除创建的秘境
	 * 
	 * @param index
	 * @return
	 */
	public boolean deleteGroupSecretData(GroupSecretData data) {
		return createList.remove(data);
	}

	/**
	 * 获取创建秘境的数量
	 * 
	 * @return
	 */
	@JsonIgnore
	public int getCreateGroupSecretSize() {
		return createList.size();
	}
}