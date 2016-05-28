package com.rwbase.dao.groupsecret.pojo.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.army.CurAttrData;

/*
 * @author HC
 * @date 2016年5月26日 下午4:07:07
 * @Description 秘境使用的阵容信息
 */
@Table(name = "")
public class GroupSecretTeamData {
	@Id
	private String userId;// 角色Id
	private List<String> defendHeroList;// 已经驻守的英雄列表
	private Map<String, CurAttrData> useHeroMap;// 用于攻打其他秘境的英雄列表

	public GroupSecretTeamData() {
		defendHeroList = new ArrayList<String>();
		useHeroMap = new HashMap<String, CurAttrData>();
	}

	// ////////////////////////////////////////////////逻辑Get区
	public String getUserId() {
		return userId;
	}

	public List<String> getDefendHeroList() {
		return defendHeroList;
	}

	public Map<String, CurAttrData> getUseHeroMap() {
		return useHeroMap;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDefendHeroList(List<String> defendHeroList) {
		this.defendHeroList = defendHeroList;
	}

	public void setUseHeroMap(Map<String, CurAttrData> useHeroMap) {
		this.useHeroMap = useHeroMap;
	}

	// ////////////////////////////////////////////////逻辑区

	public void addDefendHeroId(String heroId) {
		this.defendHeroList.add(heroId);
	}

	/**
	 * 添加要使用的阵容信息
	 * 
	 * @param heroIdList
	 */
	public void addDefendHeroIdList(List<String> heroIdList) {
		if (heroIdList.isEmpty()) {
			return;
		}

		for (int i = 0, size = heroIdList.size(); i < size; i++) {
			String id = heroIdList.get(i);
			if (!this.defendHeroList.contains(id)) {
				this.defendHeroList.add(id);
			}
		}
	}

	/**
	 * 添加移除的阵容信息
	 * 
	 * @param heroIdList
	 */
	public void removeDefendHeroIdList(List<String> heroIdList, String nonRemoveId) {
		if (heroIdList.isEmpty()) {
			return;
		}

		for (int i = 0, size = heroIdList.size(); i < size; i++) {
			String id = heroIdList.get(i);
			if (!id.equals(nonRemoveId) && defendHeroList.contains(id)) {
				defendHeroList.remove(id);
			}
		}
	}
}