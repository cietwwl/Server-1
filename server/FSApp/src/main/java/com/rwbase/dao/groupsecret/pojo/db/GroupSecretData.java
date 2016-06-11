package com.rwbase.dao.groupsecret.pojo.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;

/*
 * @author HC
 * @date 2016年5月26日 下午2:41:06
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretData {
	@Id
	private int id;// 秘境的Id
	private String userId;// 创建秘境的角色Id
	private String groupId;// 创建秘境的角色的Id
	private long createTime;// 创建秘境的时间
	private int robTimes;// 已经被掠夺的次数
	private int secretId;// 秘境配置Id
	private List<String> inviteList;// 邀请驻守的成员Id
	@SaveAsJson
	private ConcurrentHashMap<Integer, DefendUserInfoData> defendMap;// 驻守的信息
	@NonSave
	private AtomicInteger version = new AtomicInteger();

	public GroupSecretData() {
		inviteList = new ArrayList<String>();// 邀请驻守的列表
		defendMap = new ConcurrentHashMap<Integer, DefendUserInfoData>();
	}

	// ////////////////////////////////////////////////逻辑Get区
	public int getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public int getRobTimes() {
		return robTimes;
	}

	public int getSecretId() {
		return secretId;
	}

	public List<String> getInviteList() {
		return inviteList;
	}

	public ConcurrentHashMap<Integer, DefendUserInfoData> getDefendMap() {
		return defendMap;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public void setRobTimes(int robTimes) {
		this.robTimes = robTimes;
	}

	public void setSecretId(int secretId) {
		this.secretId = secretId;
	}

	public void setDefendMap(ConcurrentHashMap<Integer, DefendUserInfoData> defendMap) {
		this.defendMap = defendMap;
	}

	public void setInviteList(List<String> inviteList) {
		this.inviteList = inviteList;
	}

	// ////////////////////////////////////////////////逻辑区

	/**
	 * 增加新的防守角色信息
	 * 
	 * @param defendIndex
	 * @param data
	 */
	public boolean addDefendUserInfoData(int defendIndex, DefendUserInfoData data) {
		if (defendMap.containsKey(defendIndex)) {
			return false;
		}

		updateVersion();
		return defendMap.putIfAbsent(defendIndex, data) == null;
	}

	/**
	 * 移除自己的驻守信息
	 * 
	 * @param defendIndex
	 * @return
	 */
	public DefendUserInfoData removeDefendUserInfoData(int defendIndex) {
		updateVersion();
		return defendMap.remove(defendIndex);
	}

	/**
	 * 获取个人的防守数据
	 * 
	 * @param defendIndex
	 * @return
	 */
	@JsonIgnore
	public DefendUserInfoData getDefendUserInfoData(int defendIndex) {
		return defendMap.get(defendIndex);
	}

	/**
	 * 获取所有的驻守点
	 * 
	 * @return
	 */
	@JsonIgnore
	public Enumeration<Integer> getEnumerationKeys() {
		return defendMap.keys();
	}

	/**
	 * 所有的驻守信息
	 * 
	 * @return
	 */
	@JsonIgnore
	public Enumeration<DefendUserInfoData> getEnumerationValues() {
		return defendMap.elements();
	}

	/**
	 * 增加驻守秘境的成员列表
	 * 
	 * @param idList
	 */
	public void addInviteHeroList(List<String> idList) {
		if (idList.isEmpty()) {
			return;
		}

		for (int i = 0, size = idList.size(); i < size; i++) {
			String id = idList.get(i);
			if (!inviteList.contains(id)) {
				inviteList.add(id);
			}
		}

		updateVersion();
	}

	/**
	 * 获取当前的版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		return version.get();
	}

	/**
	 * 增加一个版本号
	 */
	public void updateVersion() {
		version.incrementAndGet();
	}
}