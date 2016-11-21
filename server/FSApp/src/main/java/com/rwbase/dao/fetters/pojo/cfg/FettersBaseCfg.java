package com.rwbase.dao.fetters.pojo.cfg;

import java.util.Collections;
import java.util.List;

/*
 * @author HC
 * @date 2016年4月27日 上午10:46:11
 * @Description 羁绊的配置
 */
public class FettersBaseCfg {
	private int fettersId;// 唯一的Id
	private int heroModelId;// 英雄的模版Id
	private String fettersConditions;// 羁绊条件Id列表
	private String fettersHeroId;// 构成羁绊的英雄Id列表
	private int seq; // 羁绊在面板的位置，相当于一个部位概念，用于战斗力运算
	private List<Integer> fettersHeroIdList;

	/**
	 * 获取羁绊Id
	 * 
	 * @return
	 */
	public int getFettersId() {
		return fettersId;
	}

	/**
	 * 获取英雄的ModelId
	 * 
	 * @return
	 */
	public int getHeroModelId() {
		return heroModelId;
	}

	/**
	 * 获取羁绊条件列表
	 * 
	 * @return
	 */
	public String getFettersConditions() {
		return fettersConditions;
	}

	/**
	 * 构成羁绊的英雄Id列表
	 * 
	 * @return
	 */
	public String getFettersHeroId() {
		return fettersHeroId;
	}
	
	/**
	 * 
	 * 羁绊在面板的位置，相当于一个部位概念，用于战斗力运算
	 * 
	 * @return
	 */
	public int getSeq() {
		return seq;
	}

	public List<Integer> getFettersHeroIdList() {
		return fettersHeroIdList;
	}

	public void setFettersHeroIdList(List<Integer> fettersHeroIdList) {
		this.fettersHeroIdList = Collections.unmodifiableList(fettersHeroIdList);
	}
}