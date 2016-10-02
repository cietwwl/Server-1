package com.rwbase.dao.fetters.pojo;

/*
 * @author HC
 * @date 2016年4月28日 下午3:08:38
 * @Description //检查某个强制限定类型是否已经要占用一个英雄Id
 */
public interface IFettersCheckForceUseHeroId {

	/**
	 * 检查某个条件是否有强制占用的英雄Id
	 * 
	 * @param subConditionId
	 * @return
	 */
	public int checkForceUseHeroId(int subConditionId);

	/**
	 * 获取检查的类型
	 * 
	 * @return
	 */
	public int getCheckConditionType();
}