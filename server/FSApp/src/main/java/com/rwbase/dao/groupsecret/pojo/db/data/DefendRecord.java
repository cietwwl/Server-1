package com.rwbase.dao.groupsecret.pojo.db.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2016年5月26日 下午2:51:25
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefendRecord {
	private int id;
	private int zoneId;// 区ID
	private String zoneName;// 区服名字
	private String groupName;// 帮派名字
	private int defenceTimes;// 防守波数
	private int secretId;// 秘境的配置Id
	private int robTime;// 被掠夺的时间
	private boolean hasKey;// 是否还有可以领取的钥石

	// ////////////////////////////////////////////////逻辑Get区
	public int getId() {
		return id;
	}

	public int getZoneId() {
		return zoneId;
	}

	public String getZoneName() {
		return zoneName;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getDefenceTimes() {
		return defenceTimes;
	}

	public int getSecretId() {
		return secretId;
	}

	public int getRobTime() {
		return robTime;
	}

	public boolean isHasKey() {
		return hasKey;
	}

	// ////////////////////////////////////////////////逻辑Set区

	public void setId(int id) {
		this.id = id;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setDefenceTimes(int defenceTimes) {
		this.defenceTimes = defenceTimes;
	}

	public void setSecretId(int secretId) {
		this.secretId = secretId;
	}

	public void setRobTime(int robTime) {
		this.robTime = robTime;
	}

	public void setHasKey(boolean hasKey) {
		this.hasKey = hasKey;
	}
}