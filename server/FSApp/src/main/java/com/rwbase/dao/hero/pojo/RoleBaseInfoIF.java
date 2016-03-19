package com.rwbase.dao.hero.pojo;


public interface RoleBaseInfoIF {

	/**
	 * 获取佣兵ID
	 * @return
	 */
	public String getId();

	/**
	 * 获取佣兵模板ID
	 * @return
	 */
	public String getTemplateId();

	/**
	 * 获取佣兵等级
	 * @return
	 */
	public int getLevel();

	/**
	 * 获取佣兵星级
	 * @return
	 */
	public int getStarLevel();

	/**
	 * 获取佣兵品质
	 * @return
	 */
	public String getQualityId();

	/**
	 * 获取佣兵当前经验
	 * @return
	 */
	public long getExp();


	/**
	 * 获取佣兵模型ID
	 * @return
	 */
	public int getModeId();




}
