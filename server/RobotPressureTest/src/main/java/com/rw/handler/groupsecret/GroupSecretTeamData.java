package com.rw.handler.groupsecret;

import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynItem;

public class GroupSecretTeamData implements SynItem {
	private String userId;// 角色Id
	private List<String> defendHeroList;// 已经驻守的英雄列表
	private Map<String, HeroLeftInfoSynData> useHeroMap;// 用于攻打其他秘境的英雄列表
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getDefendHeroList() {
		return defendHeroList;
	}
	public void setDefendHeroList(List<String> defendHeroList) {
		this.defendHeroList = defendHeroList;
	}
	public Map<String, HeroLeftInfoSynData> getUseHeroMap() {
		return useHeroMap;
	}
	public void setUseHeroMap(Map<String, HeroLeftInfoSynData> useHeroMap) {
		this.useHeroMap = useHeroMap;
	}
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return userId;
	}
	
	
}
