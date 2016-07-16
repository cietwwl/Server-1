package com.playerdata.hero;

/**
 * 
 * 英雄数据委托
 * 
 * @author CHEN.P
 *
 */
public interface IHeroDelegator {
	
	/**
	 * 英雄类型：主英雄
	 */
	public static final int HERO_TYPE_MAIN = 1;
	
	/**
	 * 英雄类型：一般英雄
	 */
	public static final int HERO_TYPE_COMMON = 1 << 1;

	/**
	 * 
	 * 获取英雄的名字
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * 获取英雄的主人的userId
	 * 
	 * @return
	 */
	public String getOwnerUserId();

	/**
	 * 
	 */
	public int getLevel();
	
	/**
	 * 
	 * 获取英雄的当前经验值
	 * 
	 * @return
	 */
	public int getExp();
	
	/**
	 * 
	 * 获取职业类型
	 * 
	 * @return
	 */
	public int getCareerType();
	
}
