package com.rw.handler.groupFight.data;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;
import com.rw.handler.battle.army.CurAttrData;
import com.rw.handler.groupFight.dataForClient.DefendArmySimpleInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGFightOnlineData implements SynItem{

	private String id;
	
	private int resourceID;
	
	private List<CurAttrData> selfHerosInfo = new ArrayList<CurAttrData>();
	
	private List<String> activeHeros = new ArrayList<String>();
	
	private int changeEnimyTimes;
	
	private DefendArmySimpleInfo randomDefender;

	private int killCount;
	
	private int hurtTotal;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public List<CurAttrData> getSelfHerosInfo() {
		return selfHerosInfo;
	}

	public void setSelfHerosInfo(List<CurAttrData> selfHerosInfo) {
		this.selfHerosInfo = selfHerosInfo;
	}
	
	public CurAttrData getSelfHeroInfo(String heroID) {
		if(selfHerosInfo == null) selfHerosInfo = new ArrayList<CurAttrData>();
		for(CurAttrData hero : selfHerosInfo)
			if(hero.getId().equals(heroID)) return hero;
		return null;
	}
	
	public List<String> getActiveHeros() {
		return activeHeros;
	}

	public void setActiveHeros(List<String> activeHeros) {
		this.activeHeros = activeHeros;
	}

	public int getChangeEnimyTimes() {
		return changeEnimyTimes;
	}

	public void setChangeEnimyTimes(int changeEnimyTimes) {
		this.changeEnimyTimes = changeEnimyTimes;
	}
	
	public void addChangeEnimyTimes() {
		this.changeEnimyTimes++;
	}

	public DefendArmySimpleInfo getRandomDefender() {
		return randomDefender;
	}

	public void setRandomDefender(DefendArmySimpleInfo randomDefender) {
		this.randomDefender = randomDefender;
	}

	public int getKillCount() {
		return killCount;
	}

	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}
	
	public void addKillCount() {
		this.killCount++;
	}

	public int getHurtTotal() {
		return hurtTotal;
	}

	public void setHurtTotal(int hurtTotal) {
		this.hurtTotal = hurtTotal;
	}
	
	public void addHurtTotal(int newHurt){
		this.hurtTotal += newHurt;
	}
	
	public void resetLoopData(){
		resourceID = 0;
		selfHerosInfo.clear();
		changeEnimyTimes = 0;
		randomDefender = null;
		killCount = 0;
		hurtTotal = 0;
	}
}
