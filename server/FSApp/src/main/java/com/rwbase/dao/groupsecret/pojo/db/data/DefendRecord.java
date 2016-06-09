package com.rwbase.dao.groupsecret.pojo.db.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月26日 下午2:51:25
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class DefendRecord {
	private int id;
	private String name;// 名字
	private int zoneId;// 区ID
	private String zoneName;// 区服名字
	private String groupName;// 帮派名字
	private int defenceTimes;// 防守波数
	private int secretId;// 秘境的配置Id
	private long robTime;// 被掠夺的时间
	private int robRes;// 掠夺的资源
	private int dropDiamond;// 掉落的钻石
	private int robGS;// 掠夺的帮派物资
	private int robGE;// 掠夺的帮派经验
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

	public long getRobTime() {
		return robTime;
	}

	public int getRobRes() {
		return robRes;
	}

	public int getDropDiamond() {
		return dropDiamond;
	}

	public int getRobGS() {
		return robGS;
	}

	public int getRobGE() {
		return robGE;
	}

	public boolean isHasKey() {
		return hasKey;
	}

	public String getName() {
		return name;
	}

	// ////////////////////////////////////////////////逻辑Set区

	public void setName(String name) {
		this.name = name;
	}

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

	public void setRobTime(long robTime) {
		this.robTime = robTime;
	}

	public void setRobRes(int robRes) {
		this.robRes = robRes;
	}

	public void setDropDiamond(int dropDiamond) {
		this.dropDiamond = dropDiamond;
	}

	public void setRobGS(int robGS) {
		this.robGS = robGS;
	}

	public void setRobGE(int robGE) {
		this.robGE = robGE;
	}

	public void setHasKey(boolean hasKey) {
		this.hasKey = hasKey;
	}
}