package com.rwbase.dao.anglearray.pojo.db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.army.CurAttrData;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/*
 * @author HC
 * @date 2016年4月20日 下午8:50:23
 * @Description 记录敌人的血量变化信息
 */
@Table(name = "angel_array_enemy_info")
public class AngelArrayEnemyInfoData implements IMapItem {
	@Id
	private String id;
	private String userId;
	private int floor;
	/** <对应的ArmyHero中的Id，CurAttribute当前剩余的属性> */
	@CombineSave(Column = "enemyChange")
	private Map<String, CurAttrData> enemyChange;// 敌人剩余的血量信息

	public AngelArrayEnemyInfoData() {
		enemyChange = new HashMap<String, CurAttrData>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public Map<String, CurAttrData> getEnemyChangeMap() {
		return new HashMap<String, CurAttrData>(enemyChange);
	}

	public void setEnemyChangeMap(Map<String, CurAttrData> enemyChange) {
		this.enemyChange = enemyChange;
	}

	/**
	 * 存储英雄的血量信息变化
	 * 
	 * @param heroId
	 * @param attrData
	 */
	public void updateHeroAttrData(String heroId, CurAttrData attrData) {
		enemyChange.put(heroId, attrData);
	}

	/**
	 * 获取某个英雄当前的血量信息
	 * 
	 * @param heroId
	 * @return
	 */
	public CurAttrData getHeroAttrData(String heroId) {
		return enemyChange.get(heroId);
	}
}