package com.rw.handler.groupFight.armySimple;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.handler.battle.army.CurAttrData;


/**
 * 战斗用临时数据，不能持久化
 * @author Administrator
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyHeroSimple {
	
	private String id;    //英雄uuid	

	private int modeId;//英雄模型Id
	private int level;//等级
	private int starLevel;//星级
	private String qualityId;//品阶Id
	
	private CurAttrData curAttrData = new CurAttrData();
	private int fighting;

	public String getId() {
		return id;
	}
	
	public int getModeId() {
		return modeId;
	}
	
	public int getLevel() {
		return level;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public String getQualityId() {
		return qualityId;
	}

	public CurAttrData getCurAttrData() {
		return curAttrData;
	}

	public int getFighting() {
		return fighting;
	}
}
