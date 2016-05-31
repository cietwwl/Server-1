package com.rwbase.dao.groupsecret.pojo.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.groupsecret.syndata.base.HeroLeftInfoSynData;

/*
 * @author HC
 * @date 2016年5月26日 下午4:07:07
 * @Description 秘境使用的阵容信息
 */
@SynClass
public class GroupSecretTeamData {
	@Id
	private String userId;// 角色Id
	private List<String> defendHeroList;// 已经驻守的英雄列表
	private Map<String, HeroLeftInfoSynData> useHeroMap;// 用于攻打其他秘境的英雄列表

	public GroupSecretTeamData() {
		defendHeroList = new ArrayList<String>();
		useHeroMap = new HashMap<String, HeroLeftInfoSynData>();
	}

	// ////////////////////////////////////////////////逻辑Get区
	public String getUserId() {
		return userId;
	}

	public List<String> getDefendHeroList() {
		return defendHeroList;
	}

	public Map<String, HeroLeftInfoSynData> getUseHeroMap() {
		return useHeroMap;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDefendHeroList(List<String> defendHeroList) {
		this.defendHeroList = defendHeroList;
	}

	public void setUseHeroMap(Map<String, HeroLeftInfoSynData> useHeroMap) {
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

	/**
	 * 检查要更换的阵容列表是否已经被其他的占用
	 * 
	 * @param heroIdList
	 * @param nonCheckId
	 * @return
	 */
	public boolean checkTeamHeroListHasExist(List<String> heroIdList, String nonCheckId) {
		if (heroIdList.isEmpty()) {
			return false;
		}

		for (int i = 0, size = heroIdList.size(); i < size; i++) {
			String id = heroIdList.get(i);
			if (!id.equals(nonCheckId) && defendHeroList.contains(id)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 改变阵容数据
	 * 
	 * @param changeList
	 * @param nonCheckId
	 */
	public void changeTeamHeroList(List<String> changeList, String nonCheckId) {
		if (changeList.isEmpty()) {
			return;
		}

		for (int i = 0, size = changeList.size(); i < size; i++) {
			String id = changeList.get(i);
			if (id.equals(nonCheckId)) {
				continue;
			}

			if (defendHeroList.contains(id)) {
				defendHeroList.remove(id);
			} else {
				defendHeroList.add(id);
			}
		}
	}
}