package com.rwbase.dao.groupsecret.pojo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.common.teamsyn.HeroLeftInfoSynData;
import com.rwproto.GroupSecretProto.GroupSecretIndex;

/*
 * @author HC
 * @date 2016年5月26日 下午3:08:51
 * @Description 秘境匹配到的敌人信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GroupSecretMatchEnemyData {
	private int id;// 搜索到的秘境Id
	@Id
	private String matchUserId;// 匹配到的人Id
	private String userId;// 搜索人的Id
	private long matchTime;// 搜索到的时间
	private long atkTime;// 攻击的时间
	private int cfgId;// 搜索到的秘境类型
	private Map<String, HeroLeftInfoSynData> teamOneMap;// 防守的一队敌人血量信息
	private Map<String, HeroLeftInfoSynData> teamTwoMap;// 防守的二队敌人血量信息
	private Map<String, HeroLeftInfoSynData> teamThreeMap;// 防守的三队敌人血量信息
	private int[] robRes = new int[3];// 可以掠夺的资源数量
	private int[] robGS = new int[3];// 可以掠夺的帮派物资
	private int[] robGE = new int[3];// 可以掠夺的帮派经验
	private int[] atkTimes = new int[3];// 攻击每个驻守点敌人的次数
	private boolean isBeat = false;// 是否已经抢到了
	private int zoneId;// 区ID
	private String zoneName;// 区名字
	@NonSave
	private AtomicInteger version = new AtomicInteger(-1);

	public GroupSecretMatchEnemyData() {
		teamOneMap = new HashMap<String, HeroLeftInfoSynData>(5);
		teamTwoMap = new HashMap<String, HeroLeftInfoSynData>(5);
		teamThreeMap = new HashMap<String, HeroLeftInfoSynData>(5);
	}

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

	public long getMatchTime() {
		return matchTime;
	}

	public int[] getRobRes() {
		return robRes;
	}

	public int[] getRobGS() {
		return robGS;
	}

	public int[] getRobGE() {
		return robGE;
	}

	public int[] getAtkTimes() {
		return atkTimes;
	}

	public int getCfgId() {
		return cfgId;
	}

	public int getZoneId() {
		return zoneId;
	}

	public String getZoneName() {
		return zoneName;
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

	public void setMatchTime(long matchTime) {
		this.matchTime = matchTime;
	}

	public void setCfgId(int cfgId) {
		this.cfgId = cfgId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
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
	 * 设置偷取某个防守点的资源，保底是1
	 * 
	 * @param index
	 * @param value
	 */
	@JsonIgnore
	public void setRobResValue(int index, int value) {
		updateVersion();
		robRes[index - 1] = value <= 0 ? 1 : value;
	}

	/**
	 * 设置偷取某个防守点的帮派物资，保底是1
	 * 
	 * @param index
	 * @param value
	 */
	@JsonIgnore
	public void setRobGSValue(int index, int value) {
		updateVersion();
		robGS[index - 1] = value <= 0 ? 1 : value;
	}

	/**
	 * 设置偷取某个防守点的帮派经验，保底是1
	 * 
	 * @param index
	 * @param value
	 */
	@JsonIgnore
	public void setRobGEValue(int index, int value) {
		updateVersion();
		robGE[index - 1] = value <= 0 ? 1 : value;
	}

	/**
	 * 设置攻击次数
	 * 
	 * @param index
	 */
	@JsonIgnore
	public void setAttackTimes(int index) {
		atkTimes[index - 1] += 1;
	}

	/**
	 * 获取攻击波数
	 * 
	 * @param index
	 * @return
	 */
	@JsonIgnore
	public int getAttackTimes(int index) {
		return atkTimes[index - 1];
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
		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = getTeamMap(index);

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
	 * 获取镇守的阵容Id列表
	 * 
	 * @param index
	 * @return
	 */
	@JsonIgnore
	public List<String> getDefendHeroIdList(int index) {
		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = getTeamAttrInfoMap(index);
		Iterator<String> itr = teamAttrInfoMap.keySet().iterator();

		List<String> heroIdList = new ArrayList<String>(teamAttrInfoMap.size());
		while (itr.hasNext()) {
			heroIdList.add(itr.next());
		}

		return heroIdList;
	}

	/**
	 * 检查是否还有英雄活着
	 * 
	 * @return
	 */
	public boolean checkHasHeroAlive() {
		if (checkDefnedIndexHasAlive(GroupSecretIndex.MAIN_VALUE) || checkDefnedIndexHasAlive(GroupSecretIndex.LEFT_VALUE) || checkDefnedIndexHasAlive(GroupSecretIndex.RIGHT_VALUE)) {
			return true;
		}

		return false;
	}

	/**
	 * 检查阵容中还有活着的英雄
	 * 
	 * @param index
	 * @return
	 */
	public boolean checkDefnedIndexHasAlive(int index) {
		Map<String, HeroLeftInfoSynData> teamAttrInfoMap = getTeamMap(index);
		for (Entry<String, HeroLeftInfoSynData> e : teamAttrInfoMap.entrySet()) {
			HeroLeftInfoSynData value = e.getValue();
			if (value == null || value.getLife() > 0) {
				return true;
			}
		}

		return false;
	}

	private Map<String, HeroLeftInfoSynData> getTeamMap(int index) {
		if (index == GroupSecretIndex.LEFT_VALUE) {
			return teamTwoMap;
		} else if (index == GroupSecretIndex.MAIN_VALUE) {
			return teamOneMap;
		} else {
			return teamThreeMap;
		}
	}

	/**
	 * 清除缓存的数据
	 */
	@JsonIgnore
	public void clearAllData() {
		matchUserId = "";
		id = 0;
		matchTime = 0;
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

		for (int i = 0, len = atkTimes.length; i < len; i++) {
			atkTimes[i] = 0;
		}

		updateVersion();
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		return version.get();
	}

	/**
	 * 更新版本号
	 */
	public void updateVersion() {
		version.incrementAndGet();
	}
}