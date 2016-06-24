package com.playerdata.groupFightOnline.data;

import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.CurAttrData;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataForClient.DefendArmySimpleInfo;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGFightOnlineData {

	@Id
	private String id;
	
	@CombineSave
	private int resourceID;
	
	@CombineSave
	private List<CurAttrData> selfHerosInfo;
	
	@CombineSave
	private List<String> activeHeros;
	
	@CombineSave
	private int changeEnimyTimes;
	
	@CombineSave
	private DefendArmySimpleInfo randomDefender;

	@CombineSave
	private int killCount;
	
	@CombineSave
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

	public List<CurAttrData> getSelfArmyInfo() {
		return selfHerosInfo;
	}

	public void setSelfArmyInfo(List<CurAttrData> selfHerosInfo) {
		this.selfHerosInfo = selfHerosInfo;
	}
	
	public CurAttrData getSelfHeroInfo(String heroID) {
		for(CurAttrData hero : selfHerosInfo)
			if(hero.getId().equals(heroID)) return hero;
		return null;
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
}
