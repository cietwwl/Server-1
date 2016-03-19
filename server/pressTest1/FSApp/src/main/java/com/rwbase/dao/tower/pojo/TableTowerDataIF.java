package com.rwbase.dao.tower.pojo;

import java.util.Enumeration;
import java.util.List;

public interface TableTowerDataIF {
	/**
	 * / 用户ID
	 * @return
	 */
	public String getUserId(); 
	
	/**
	 * 玩家每天刷新对手时的战斗力
	 * @return
	 */
	public int getFighting() ;
	
	/**
	 * //当前塔层
	 * @return
	 */
	public int getCurrTowerID();
	
	/**
	 * //刷新次数
	 * @return
	 */
	public int getRefreshTimes();
	
//	public Enumeration<? extends TowerEnemyInfoIF> getEnemyEnumeration();
	//public ConcurrentHashMap<Integer, TowerEnemyInfo> getEnemyList();
	/**
	 *  玩家每天刷新对手时的等级
	 * @return
	 */
	public int getLevel();
	
	/**
	 *  玩家血量变化记录
	 * @return
	 */
	public List<? extends TowerHeroChangeIF> getHeroChageList();
	
	/**
	 * 开放塔层数据
	 * @return
	 */
	public List<Boolean> getOpenTowerList();
	
	/**
	 * 第一次领取奖励数据
	 * @return
	 */
	public List<Boolean> getFirstTowerList();
	
	/**
	 *领取奖品层数据
	 * @return
	 */
	public List<Boolean> getAwardTowerList();
	
	/**
	 *打败列表
	 * @return
	 */
	public List<Boolean> getBeatTowerList();
}
