package com.rwbase.dao.battletower.pojo.readonly;

/*
 * @author HC
 * @date 2015年9月3日 下午5:58:09
 * @Description 试练塔中佣兵信息接口
 */
public interface BattleTowerHeroInfoIF {
	public boolean isMainRole();

	/**
	 * 获取佣兵的Id
	 * 
	 * @return
	 */
	public String getHeroId();

	/**
	 * 获取佣兵的等级
	 * 
	 * @return
	 */
	public int getLevel();

	/**
	 * 获取佣兵的品质
	 * 
	 * @return
	 */
	public int getQuality();
	
	public String getQualityId();

	/**
	 * 获取佣兵的星数
	 * 
	 * @return
	 */
	public int getStarNum();
}