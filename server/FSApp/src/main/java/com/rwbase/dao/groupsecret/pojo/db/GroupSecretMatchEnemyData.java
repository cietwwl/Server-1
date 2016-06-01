package com.rwbase.dao.groupsecret.pojo.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.rwbase.common.teamsyn.HeroLeftInfoSynData;
import com.rwproto.GroupSecretProto.GroupSecretIndex;

/*
 * @author HC
 * @date 2016年5月26日 下午3:08:51
 * @Description 秘境匹配到的敌人信息
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GroupSecretMatchEnemyData {
	private int id;// 搜索到的秘境Id
	@Id
	private String matchUserId;// 匹配到的人Id
	private String userId;// 搜索人的Id
	private long atkTime;// 攻击的时间
	private Map<String, HeroLeftInfoSynData> teamOneMap;// 防守的一队敌人血量信息
	private Map<String, HeroLeftInfoSynData> teamTwoMap;// 防守的二队敌人血量信息
	private Map<String, HeroLeftInfoSynData> teamThreeMap;// 防守的三队敌人血量信息
	private int[] robRes = new int[3];// 可以掠夺的资源数量
	private int[] robGS = new int[3];// 可以掠夺的帮派物资
	private int[] robGE = new int[3];// 可以掠夺的帮派经验
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
	@JsonIgnore
	public Map<String, HeroLeftInfoSynData> getTeamAttrInfoMap(int defendIndex) {
		if (defendIndex == GroupSecretIndex.LEFT_VALUE) {
			if (teamTwoMap == null) {
				return Collections.emptyMap();
			}

			return new HashMap<String, HeroLeftInfoSynData>(teamTwoMap);
		} else if (defendIndex == GroupSecretIndex.MAIN_VALUE) {
			if (teamOneMap == null) {
				return Collections.emptyMap();
			}

			return new HashMap<String, HeroLeftInfoSynData>(teamOneMap);
		} else if (defendIndex == GroupSecretIndex.RIGHT_VALUE) {
			if (teamThreeMap == null) {
				return Collections.emptyMap();
			}

			return new HashMap<String, HeroLeftInfoSynData>(teamThreeMap);
		} else {
			return Collections.emptyMap();
		}
	}

	@JsonIgnore
	public int getAllRobResValue() {
		int value = 0;
		for (int i = 0, len = robRes.length; i < len; i++) {
			value += robRes[i];
		}
		return value;
	}

	@JsonIgnore
	public int getAllRobGSValue() {
		int value = 0;
		for (int i = 0, len = robGS.length; i < len; i++) {
			value += robGS[i];
		}
		return value;
	}

	@JsonIgnore
	public int getAllRobGEValue() {
		int value = 0;
		for (int i = 0, len = robGE.length; i < len; i++) {
			value += robGE[i];
		}
		return value;
	}

	@JsonIgnore
	public int getRobResValue(int index) {
		return robRes[index - 1];
	}

	@JsonIgnore
	public int getRobGSValue(int index) {
		return robGS[index - 1];
	}

	@JsonIgnore
	public int getRobGEValue(int index) {
		return robGE[index - 1];
	}

	/**
	 * 设置偷取某个防守点的资源
	 * 
	 * @param index
	 * @param value
	 */
	@JsonIgnore
	public void setRobResValue(int index, int value) {
		robRes[index - 1] = value;
	}

	/**
	 * 设置偷取某个防守点的帮派物资
	 * 
	 * @param index
	 * @param value
	 */
	@JsonIgnore
	public void setRobGSValue(int index, int value) {
		robGS[index - 1] = value;
	}

	/**
	 * 设置偷取某个防守点的帮派经验
	 * 
	 * @param index
	 * @param value
	 */
	@JsonIgnore
	public void setRobGEValue(int index, int value) {
		robGE[index - 1] = value;
	}

	/**
	 * 初始化匹配到的阵容的血量信息，开始的时候所有的血量变化都是Null
	 * 
	 * @param index
	 * @param leftMap
	 */
	@JsonIgnore
	public void initHeroLeftInfo(int index, Map<String, HeroLeftInfoSynData> leftMap) {
		if (index == GroupSecretIndex.MAIN_VALUE) {
			this.teamOneMap = new HashMap<String, HeroLeftInfoSynData>(leftMap);
		} else if (index == GroupSecretIndex.LEFT_VALUE) {
			this.teamTwoMap = new HashMap<String, HeroLeftInfoSynData>(leftMap);
		} else if (index == GroupSecretIndex.RIGHT_VALUE) {
			this.teamThreeMap = new HashMap<String, HeroLeftInfoSynData>(leftMap);
		}
	}

	/**
	 * 更新英雄的剩余血量
	 * 
	 * @param index 索引
	 * @param heroId 英雄Id
	 * @param heroLeftInfo 剩余的英雄血量
	 * @return 返回是否更新血量成功
	 */
	@JsonIgnore
	public boolean updateHeroLeftInfo(int index, String heroId, HeroLeftInfoSynData heroLeftInfo) {
		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = getTeamAttrInfoMap(index);
		if (teamAttrInfoMap == null) {
			return true;
		}

		if (!teamAttrInfoMap.containsKey(heroId)) {// 是否已经包含了英雄
			return false;
		}

		teamAttrInfoMap.put(heroId, heroLeftInfo);
		return true;
	}

	/**
	 * 获取是否某个驻守点包含了某些英雄的数据
	 * 
	 * @param index 驻守点
	 * @param heroId 英雄Id
	 * @return
	 */
	@JsonIgnore
	public boolean checkTeamHasHeroId(int index, String heroId) {
		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = getTeamAttrInfoMap(index);
		if (teamAttrInfoMap == null) {
			return false;
		}

		return teamAttrInfoMap.containsKey(heroId);
	}

	/**
	 * 清除缓存的数据
	 */
	@JsonIgnore
	public void clearAllData() {
		matchUserId = "";
		id = 0;
		atkTime = 0;
		isBeat = false;

		teamOneMap.clear();
		teamTwoMap.clear();
		teamThreeMap.clear();

		for (int i = 0, len = robRes.length; i < len; i++) {
			robRes[i] = 0;
		}

		for (int i = 0, len = robGE.length; i < len; i++) {
			robGE[i] = 0;
		}

		for (int i = 0, len = robGS.length; i < len; i++) {
			robGS[i] = 0;
		}
	}
}