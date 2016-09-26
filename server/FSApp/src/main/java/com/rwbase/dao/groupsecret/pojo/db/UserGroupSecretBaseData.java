package com.rwbase.dao.groupsecret.pojo.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月26日 下午2:34:44
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class UserGroupSecretBaseData {
	@Id
	private String userId;// 角色的Id
	private int keyCount;// 当前钥石数量
	private long lastRecoveryTime;// 上次恢复钥石的时间
//	@IgnoreSynField
//	private List<String> defendSecretIdList;// 驻守的秘境Id列表
	@IgnoreSynField
	private Map<Integer, String> defendSecretIdMap; // 驻守的秘境Id列表
	private String matchSecretId;// 探索到的秘境Id
	// =============================每天5点就要重置的数据
	private int receiveKeyCount;// 当天领取钥石的数量
	private int buyKeyTimes;// 当天购买钥石的次数
	private int matchTimes;// 当天匹配秘境的次数
	@IgnoreSynField
	private long lastResetTime;// 上次重置的时间点

	public UserGroupSecretBaseData() {
//		defendSecretIdList = new ArrayList<String>();
		defendSecretIdMap = new HashMap<Integer, String>();
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setKeyCount(int keyCount) {
		this.keyCount = keyCount;
	}

	public void setLastRecoveryTime(long lastRecoveryTime) {
		this.lastRecoveryTime = lastRecoveryTime;
	}

	public void setMatchSecretId(String matchSecretId) {
		this.matchSecretId = matchSecretId;
	}

	public void setReceiveKeyCount(int receiveKeyCount) {
		this.receiveKeyCount = receiveKeyCount;
	}

	public void setBuyKeyTimes(int buyKeyTimes) {
		this.buyKeyTimes = buyKeyTimes;
	}

	public void setMatchTimes(int matchTimes) {
		this.matchTimes = matchTimes;
	}

//	public void setDefendSecretIdList(List<String> defendSecretIdList) {
//		this.defendSecretIdList = defendSecretIdList;
//	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setLastResetTime(long lastResetTime) {
		this.lastResetTime = lastResetTime;
	}

	// ////////////////////////////////////////////////逻辑Get区
	public int getKeyCount() {
		return keyCount;
	}

	public long getLastRecoveryTime() {
		return lastRecoveryTime;
	}

	public String getMatchSecretId() {
		return matchSecretId;
	}

	public int getReceiveKeyCount() {
		return receiveKeyCount;
	}

	public int getBuyKeyTimes() {
		return buyKeyTimes;
	}

	public int getMatchTimes() {
		return matchTimes;
	}

	public String getUserId() {
		return userId;
	}

//	/**
//	 * 获取自己驻守的秘境Id
//	 * 
//	 * @return
//	 */
//	public List<String> getDefendSecretIdList() {
//		if (defendSecretIdList == null) {
//			return Collections.emptyList();
//		}
//
//		return new ArrayList<String>(defendSecretIdList);
//	}
	
	public Map<Integer, String> getDefendSecretIdMap() {
		return new HashMap<Integer, String>(defendSecretIdMap);
	}

	public long getLastResetTime() {
		return lastResetTime;
	}

	// ////////////////////////////////////////////////逻辑区
//	/**
//	 * 增加驻守的秘境Id
//	 * 
//	 * @param secretId
//	 */
//	public synchronized void addDefendSecretId(String secretId) {
//		defendSecretIdList.add(secretId);
//	}
	
	/**
	 * 
	 * @param pos
	 * @param secretId
	 */
	public synchronized void addDefendSecretId(int pos, String secretId) {
		defendSecretIdMap.put(pos, secretId);
	}

//	/**
//	 * 删除已经参与的驻守秘境
//	 * 
//	 * @param secretId
//	 */
//	public synchronized void removeDefendSecretId(String secretId) {
//		defendSecretIdList.remove(secretId);
//	}

//	/**
//	 * 检测是否请求的秘境Id
//	 * 
//	 * @param secretId
//	 * @return
//	 */
//	public synchronized boolean hasDefendSecretId(String secretId) {
//		return defendSecretIdList.contains(secretId);
//	}
	
	public synchronized void removeDefendSecretId(String secretId) {
		for (Iterator<Map.Entry<Integer, String>> itr = defendSecretIdMap.entrySet().iterator(); itr.hasNext();) {
			if (itr.next().getValue().equals(secretId)) {
				itr.remove();
			}
		}
	}
	
	public synchronized boolean hasDefendSecretId(String secretId) {
		return defendSecretIdMap.containsValue(secretId);
	}
	
	public synchronized boolean isPosEmpty(int pos) {
		return !defendSecretIdMap.containsKey(pos);
	}
}