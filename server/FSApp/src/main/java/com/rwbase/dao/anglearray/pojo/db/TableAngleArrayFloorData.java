package com.rwbase.dao.anglearray.pojo.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.playerdata.army.ArmyInfo;

/*
 * @author HC
 * @date 2015年11月10日 下午12:09:03
 * @Description 万仙阵个人的关卡匹配到的阵容信息
 */
@Table(name = "angle_array_floor_data")
public class TableAngleArrayFloorData {
	@Id
	private String userId;
	private ConcurrentHashMap<Integer, ArmyInfo> enemyMap;// 塔层敌人数据

	public TableAngleArrayFloorData() {
		this.enemyMap = new ConcurrentHashMap<Integer, ArmyInfo>();
	}

	public TableAngleArrayFloorData(String userId) {
		this();
		this.userId = userId;
	}

	// ///////////////////////////////////////////GET区域
	/**
	 * 获取角色Id
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取随机到的敌人的数据
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, ArmyInfo> getEnemyMap() {
		return enemyMap;
	}

	/**
	 * 获取所有敌人的数据
	 * 
	 * @return
	 */
	@JsonIgnore
	public Enumeration<ArmyInfo> getEnemyValues() {
		return this.enemyMap.elements();
	}

	// ///////////////////////////////////////////SET区域
	/**
	 * 设置角色的Id
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 设置敌人的Map
	 * 
	 * @param enemyMap
	 */
	public void setEnemyMap(ConcurrentHashMap<Integer, ArmyInfo> enemyMap) {
		this.enemyMap = enemyMap;
	}

	// ///////////////////////////////////////////逻辑区域
	/**
	 * 放入新的敌人信息
	 * 
	 * @param floor 等级
	 * @param enemyInfo 敌人信息
	 */
	public void putNewEnemyInfo(int floor, ArmyInfo enemyInfo) {
		this.enemyMap.putIfAbsent(floor, enemyInfo);
	}

	/**
	 * 更新新的敌人信息
	 * 
	 * @param floor 等级
	 * @param enemyInfo 敌人信息
	 */
	public void updateEnemyInfo(int floor, ArmyInfo enemyInfo) {
		this.enemyMap.put(floor, enemyInfo);
	}

	/**
	 * 获取敌人的信息
	 * 
	 * @param floor 等级
	 * @return
	 */
	@JsonIgnore
	public ArmyInfo getEnemyInfo(int floor) {
		return this.enemyMap.get(floor);
	}

	/**
	 * 清除所有的阵容信息
	 */
	public void clearAllEnemyInfo() {
		this.enemyMap.clear();
	}

	/**
	 * 获取敌方阵容信息
	 * 
	 * @return
	 */
	@JsonIgnore
	public Enumeration<ArmyInfo> getEnemyEnumeration() {
		return this.enemyMap.elements();
	}

	/**
	 * 获取敌人信息层数列表
	 * 
	 * @return
	 */
	@JsonIgnore
	public Enumeration<Integer> getKeyEnumeration() {
		return this.enemyMap.keys();
	}

	/**
	 * 获取匹配到的阵容的Id列表
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<String> getAllEnemyIdList() {
		List<String> idList = new ArrayList<String>();
		if (this.enemyMap == null) {
			return idList;
		}

		for (Entry<Integer, ArmyInfo> e : enemyMap.entrySet()) {
			idList.add(e.getValue().getPlayer().getRoleBaseInfo().getId());
		}

		return idList;
	}
}