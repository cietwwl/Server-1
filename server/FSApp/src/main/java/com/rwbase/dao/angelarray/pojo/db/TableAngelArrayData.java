package com.rwbase.dao.angelarray.pojo.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.tower.pojo.TowerHeroChange;

/*
 * @author HC
 * @date 2015年11月10日 上午11:15:42
 * @Description 万仙阵个人数据
 */
@Table(name = "angel_array_data")
public class TableAngelArrayData {
	@Id
	private String userId;// 角色Id
	private int maxFloor = -1;// 历史最高层
	private int curFloor;// 当前层,默认为1
	private int resetTimes;// 重置的次数
	private long resetTime;// 重置匹配数据的时间
	private int resetFighting;// 重置时的战斗力
	private int resetLevel;// 重置时的等级
	private int resetRankIndex;// 重置时的竞技场排名
	private int curFloorState = -1;// 当前的状态(0未通关,1成功未领奖)
	@CombineSave(Column = "heroChange")
	@SaveAsJson
	private Map<String, TowerHeroChange> heroChangeMap;// 玩家血量变化记录

	public TableAngelArrayData() {
		this.heroChangeMap = new HashMap<String, TowerHeroChange>();
	}

	public TableAngelArrayData(String userId) {
		this();
		this.userId = userId;
	}

	// ///////////////////////////////////////////////GET区域
	/**
	 * 获取历史中打过的最高层次 用于处理通过的关卡是不是第一次 应不应该有首战奖励
	 * 
	 * @return
	 */
	public int getMaxFloor() {
		return maxFloor;
	}

	/**
	 * 获取当前已经打到的层数
	 * 
	 * @return
	 */
	public int getCurFloor() {
		return curFloor;
	}

	/**
	 * 获取当前已经使用的重置次数
	 * 
	 * @return
	 */
	public int getResetTimes() {
		return resetTimes;
	}

	/**
	 * 获取重置万仙阵时的战斗力
	 * 
	 * @return
	 */
	public int getResetFighting() {
		return resetFighting;
	}

	/**
	 * 获取重置万仙阵时角色的等级
	 * 
	 * @return
	 */
	public int getResetLevel() {
		return resetLevel;
	}

	/**
	 * 获取重置万仙阵时角色在竞技场中的排名
	 * 
	 * @return
	 */
	public int getResetRankIndex() {
		return resetRankIndex;
	}

	/**
	 * 设置角色的Id
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置自己英雄最终剩余的属性
	 * 
	 * @return
	 */
	public Map<String, TowerHeroChange> getHeroChangeMap() {
		return new HashMap<String, TowerHeroChange>(heroChangeMap);
	}

	/**
	 * 获取当前关的状态
	 * 
	 * @return
	 */
	public int getCurFloorState() {
		return curFloorState;
	}

	/**
	 * 获取重置数据的时间
	 * 
	 * @return
	 */
	public long getResetTime() {
		return resetTime;
	}

	// ///////////////////////////////////////////////SET区域
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setMaxFloor(int maxFloor) {
		this.maxFloor = maxFloor;
	}

	public void setCurFloor(int curFloor) {
		this.curFloor = curFloor;
	}

	public void setResetTimes(int resetTimes) {
		this.resetTimes = resetTimes;
	}

	public void setResetFighting(int resetFighting) {
		this.resetFighting = resetFighting;
	}

	public void setResetLevel(int resetLevel) {
		this.resetLevel = resetLevel;
	}

	public void setResetRankIndex(int resetRankIndex) {
		this.resetRankIndex = resetRankIndex;
	}

	public void setHeroChangeMap(Map<String, TowerHeroChange> heroChangeMap) {
		this.heroChangeMap = heroChangeMap;
	}

	/**
	 * 设置关卡的状态
	 * 
	 * @param curFloorState
	 */
	public void setCurFloorState(int curFloorState) {
		this.curFloorState = curFloorState;
	}

	/**
	 * 设置重置数据时间
	 * 
	 * @param resetTime
	 */
	public void setResetTime(long resetTime) {
		this.resetTime = resetTime;
	}

	// ///////////////////////////////////////////////逻辑区域

	/**
	 * 获取佣兵属性修改
	 * 
	 * @param heroId
	 * @return
	 */
	public TowerHeroChange getHeroChange(String heroId) {
		return this.heroChangeMap.get(heroId);
	}

	// /**
	// * 更新属性修改
	// *
	// * @param heroChange
	// */
	// public void updateHeroChange(TowerHeroChange heroChange) {
	// if (heroChange == null) {
	// return;
	// }
	//
	// if (heroChangeMap.containsKey(heroChange.getRoleId())) {
	// heroChangeMap.put(heroChange.getRoleId(), heroChange);
	// }
	// }

	/**
	 * 修改角色属性修改
	 * 
	 * @param roleId
	 * @param heroChange
	 */
	public void updateHeroChange(String roleId, TowerHeroChange heroChange) {
		if (heroChange == null) {
			return;
		}

		// if (heroChangeMap.containsKey(roleId)) {
		heroChangeMap.put(roleId, heroChange);
		// }
	}

	/**
	 * 获取角色信息改变列表
	 * 
	 * @return
	 */
	public List<TowerHeroChange> getHeroChangleList() {
		return heroChangeMap == null ? new ArrayList<TowerHeroChange>() : new ArrayList<TowerHeroChange>(heroChangeMap.values());
	}

	/**
	 * 重置角色血量记录
	 */
	public void resetHeroChange() {
		this.heroChangeMap.clear();
	}
}