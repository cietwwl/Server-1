package com.rwbase.dao.fetters.pojo;

import java.util.List;
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
	private Map<Integer, List<Integer>> openList;// 开放列表

	public int getHeroModelId() {
		return heroModelId;
	}

	public void setHeroModelId(int heroModelId) {
		this.heroModelId = heroModelId;
	}

	public Map<Integer, List<Integer>> getOpenList() {
		return openList;
	}

	public void setOpenList(Map<Integer, List<Integer>> openList) {
		this.openList = openList;
	}
}