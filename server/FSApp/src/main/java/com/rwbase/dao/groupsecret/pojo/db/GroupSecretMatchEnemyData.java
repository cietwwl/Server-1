package com.rwbase.dao.groupsecret.pojo.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import com.playerdata.army.CurAttrData;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwproto.GroupSecretProto.GroupSecretIndex;

/*
 * @author HC
 * @date 2016年5月26日 下午3:08:51
 * @Description 秘境匹配到的敌人信息
 */
public class GroupSecretMatchEnemyData {
	private int id;// 搜索到的秘境Id
	@Id
	private String matchUserId;// 匹配到的人Id
	private String userId;// 搜索人的Id
	private long atkTime;// 攻击的时间
	private Map<String, CurAttrData> teamOneMap;// 防守的一队敌人血量信息
	private Map<String, CurAttrData> teamTwoMap;// 防守的二队敌人血量信息
	private Map<String, CurAttrData> teamThreeMap;// 防守的三队敌人血量信息
	@CombineSave(Column = "rob")
	private int robRes;// 可以掠夺的资源数量
	@CombineSave(Column = "rob")
	private int robGS;// 可以掠夺的帮派物资
	@CombineSave(Column = "rob")
	private int robGE;// 可以掠夺的帮派经验
	private boolean isBeat = false;// 是否已经抢到了

	// ////////////////////////////////////////////////逻辑Get区
	public int getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public long getAtkTime() {
		return atkTime;
	}

	public Map<String, CurAttrData> getTeamOneMap() {
		return teamOneMap;
	}

	public Map<String, CurAttrData> getTeamTwoMap() {
		return teamTwoMap;
	}

	public Map<String, CurAttrData> getTeamThreeMap() {
		return teamThreeMap;
	}

	public int getRobRes() {
		return robRes;
	}

	public int getRobGS() {
		return robGS;
	}

	public int getRobGE() {
		return robGE;
	}

	public boolean isBeat() {
		return isBeat;
	}

	public String getMatchUserId() {
		return matchUserId;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAtkTime(long atkTime) {
		this.atkTime = atkTime;
	}

	public void setTeamOneMap(Map<String, CurAttrData> teamOneMap) {
		this.teamOneMap = teamOneMap;
	}

	public void setTeamTwoMap(Map<String, CurAttrData> teamTwoMap) {
		this.teamTwoMap = teamTwoMap;
	}

	public void setTeamThreeMap(Map<String, CurAttrData> teamThreeMap) {
		this.teamThreeMap = teamThreeMap;
	}

	public void setRobRes(int robRes) {
		this.robRes = robRes;
	}

	public void setRobGS(int robGS) {
		this.robGS = robGS;
	}

	public void setRobGE(int robGE) {
		this.robGE = robGE;
	}

	public void setBeat(boolean isBeat) {
		this.isBeat = isBeat;
	}

	public void setMatchUserId(String matchUserId) {
		this.matchUserId = matchUserId;
	}

	// ////////////////////////////////////////////////逻辑区

	/**
	 * 
	 * 获取对应的血量变化信息
	 * 
	 * @param defendIndex {@link GroupSecretIndex}
	 * @return
	 */
	public Map<String, CurAttrData> getTeamAttrInfoMap(int defendIndex) {
		if (defendIndex == GroupSecretIndex.LEFT_VALUE) {
			if (teamTwoMap == null) {
				return Collections.emptyMap();
			}

			return new HashMap<String, CurAttrData>(teamTwoMap);
		} else if (defendIndex == GroupSecretIndex.MAIN_VALUE) {
			if (teamOneMap == null) {
				return Collections.emptyMap();
			}

			return new HashMap<String, CurAttrData>(teamOneMap);
		} else if (defendIndex == GroupSecretIndex.RIGHT_VALUE) {
			if (teamThreeMap == null) {
				return Collections.emptyMap();
			}

			return new HashMap<String, CurAttrData>(teamThreeMap);
		} else {
			return Collections.emptyMap();
		}
	}
}