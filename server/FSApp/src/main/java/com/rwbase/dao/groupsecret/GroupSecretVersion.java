package com.rwbase.dao.groupsecret;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2016年6月3日 下午4:39:38
 * @Description 客户端传递所有秘境的版本号
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretVersion {
	private Map<String, Integer> map;
	private int enemyVersion;// 敌人的版本号
	private int defendRecordVersion;// 防守记录的版本号
	private int memberVersion;// 成员的版本号

	public GroupSecretVersion() {
		map = new HashMap<String, Integer>();
	}

	public Map<String, Integer> getMap() {
		return map;
	}

	public int getEnemyVersion() {
		return enemyVersion;
	}

	public int getDefendRecordVersion() {
		return defendRecordVersion;
	}

	/**
	 * 成员的列表
	 * 
	 * @return
	 */
	public int getMemberVersion() {
		return memberVersion;
	}
}