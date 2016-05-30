package com.rwbase.dao.fetters.pojo.cfg;

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
}