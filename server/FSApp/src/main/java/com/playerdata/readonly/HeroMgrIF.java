//package com.playerdata.readonly;
//
//import java.util.Enumeration;
//import java.util.List;
//
//public interface HeroMgrIF {
//
//	/**
//	 * 获取佣兵id列表
//	 * @return
//	 */
//	public List<String> getHeroIdList();
//
////	/**
////	 * 通过模板id获取指定佣兵
////	 * @param heroId
////	 * @return
////	 */
////	public HeroIF getHeroByHeroId(String heroId);
//
//	/**
//	 * 通过佣兵id获取指定佣兵
//	 * @param uuid
//	 * @return
//	 */
//	public HeroIF getHeroById(String uuid);
//
//	/**
//	 * 通过模型ID获取指定佣兵
//	 * @param moderId
//	 * @return
//	 */
//	public HeroIF getHeroByModerId(int moderId) ;
//	/**
//	 * 获取前四个最大战力
//	 * @return
//	 */
//	public int getFightingTeam();	
//
//	/**
//	 * 获取所有用兵战力
//	 * @return
//	 */
//	public int getFightingAll();
//
//	/**
//	 * 获取所有星星
//	 * @return
//	 */
//	public int getStarAll();
//
//	/**
//	 * 检查是否有指定星级的佣兵：
//	 * @param herocount
//	 * @param star
//	 * @return
//	 */
//	public int isHasStar( int star);
//	/**
//	 * 检查是否有指定品质的佣兵：
//	 * @param herocount
//	 * @param quality
//	 * @return
//	 */
//	public int checkQuality( int quality);
//
//	/**
//	 * 获得佣兵列表最大战斗力前4个佣兵
//	 * 
//	 * @return 佣兵ID列表
//	 */
//	public List<? extends HeroIF> getMaxFightingHeros();
//
//	
//	/**
//	 * 所有英雄列表
//	 * @return
//	 */
//	public Enumeration<? extends HeroIF> getHerosEnumeration();
//	
//	/**
//	 * 所有英雄数量
//	 * @return
//	 */
//	 public int getHerosSize();
//}
