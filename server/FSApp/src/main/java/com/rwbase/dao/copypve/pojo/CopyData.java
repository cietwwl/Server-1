package com.rwbase.dao.copypve.pojo;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.readonly.CopyDataIF;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CopyData implements CopyDataIF {

	private int infoId;
	private int copyType;// 副本类型
	private int copyCount;// 副本次数
	private int resetCount;// 已重置次数
	private ConcurrentHashMap<String, Integer> passMap;// 首次通关记录，key为degreeId,value
														// -- 0为未通关，1为已通过
	private long lastFreeResetTime; // 上次免费重置次数
	private long lastChallengeTime; // 上次挑战时间

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public int getCopyType() {
		return copyType;
	}

	public void setCopyType(int copyType) {
		this.copyType = copyType;
	}

	public int getCopyCount() {
		return copyCount;
	}

	public void setCopyCount(int copyCount) {
		this.copyCount = copyCount;
	}

	public int getResetCount() {
		return resetCount;
	}

	public void setResetCount(int resetCount) {
		this.resetCount = resetCount;
	}

	public void setPassMap(ConcurrentHashMap<String, Integer> passMap) {
		this.passMap = passMap;
	}

	public Integer getPassMap(String key) {
		if (passMap == null)
			return null;
		return this.passMap.get(key);
	}

	public void addPassMap(String key, Integer value) {
		if (passMap == null)
			return;
		this.passMap.put(key, value);
	}

	@JsonIgnore
	public Enumeration<String> getPassMapKeysEnumeration() {
		if (passMap == null)
			return null;
		return passMap.keys();
	}

	@JsonIgnore
	public Enumeration<Integer> getPassMapValuesEnumeration() {
		if (passMap == null)
			return null;
		return passMap.elements();
	}

	public long getLastFreeResetTime() {
		return lastFreeResetTime;
	}

	public void setLastFreeResetTime(long lastFreeResetTime) {
		this.lastFreeResetTime = lastFreeResetTime;
	}

	public long getLastChallengeTime() {
		return lastChallengeTime;
	}

	public void setLastChallengeTime(long lastChallengeTime) {
		this.lastChallengeTime = lastChallengeTime;
	}

}
