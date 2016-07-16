package com.playerdata.hero;

import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;

public interface IHeroMgr {

	/**
	 * 
	 * 获取玩家所有的英雄的id
	 * 
	 * @param userId
	 * @return
	 */
	public List<String> getHeroIdList(Player player);
	
	/**
	 * 
	 * 根据英雄的id，获取英雄的对象
	 * 
	 * @param userId
	 * @param uuid
	 * @return
	 */
	public IHero getHeroById(Player player, String uuid);
	
	/**
	 * 
	 * 根据模型ID，获取英雄对象
	 * 
	 * @param userId
	 * @param moderId
	 * @return
	 */
	public IHero getHeroByModerId(Player player, int moderId);
	
	/**
	 * 
	 * 获取前四名的英雄的战力总和
	 * 
	 * @return
	 */
	public int getFightingTeam(Player player);
	
	/**
	 * 
	 * 获取所有英雄的总战斗力
	 * 
	 * @param userId
	 * @return
	 */
	public int getFightingAll(Player player);
	
	/**
	 * 
	 * 获取所有英雄的总星星数量
	 * 
	 * @param userId
	 * @return
	 */
	public int getStarAll(Player player);
	
	/**
	 * 
	 * 检查是否有指定星级的英雄
	 * 
	 * @param userId
	 * @param star
	 * @return
	 */
	public int IsHasStar(Player player, int star);
	
	/**
	 * 
	 * 检查是否有指定品质的英雄
	 * 
	 * @param userId
	 * @param quality
	 * @return
	 */
	public int checkQuality(Player player, int quality);
	
	/**
	 * 
	 * 获取战斗力最大的四个英雄
	 * 
	 * @param userId
	 * @return
	 */
	public List<? extends IHero> getMaxFightingHers(Player player);
	
	/**
	 * 
	 * 获取所有英雄列表
	 * 
	 * @param userId
	 * @return
	 */
	public Enumeration<? extends IHero> getHeroEnumeration(Player player);
	
	/**
	 * 
	 * 获取所有英雄数量
	 * 
	 * @param userId
	 * @return
	 */
	public int getHerosSize(Player player);
}
