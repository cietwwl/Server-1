package com.rwbase.dao.fetters.pojo;

import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年4月28日 下午8:29:05
 * @Description 
 */
@SynClass
public class SynFettersData {
	
	private int heroModelId;// 英雄的模版Id
	private Map<Integer, SynConditionData> openList;// 开放列表

	
	public int getHeroModelId() {
		return heroModelId;
	}

	public void setHeroModelId(int heroModelId) {
		this.heroModelId = heroModelId;
	}

	public Map<Integer, SynConditionData> getOpenList() {
		return openList;
	}

	public void setOpenList(Map<Integer, SynConditionData> openList) {
		this.openList = openList;
	}
}