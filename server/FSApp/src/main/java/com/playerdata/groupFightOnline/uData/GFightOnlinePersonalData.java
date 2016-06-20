package com.playerdata.groupFightOnline.uData;

import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataExtend.DefendArmySimpleInfo;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlinePersonalData {

	@Id
	private String id;
	
	private List<ArmyHeroSimple> selfArmyInfo;
	
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

	public List<ArmyHeroSimple> getSelfArmyInfo() {
		return selfArmyInfo;
	}

	public void setSelfArmyInfo(List<ArmyHeroSimple> selfArmyInfo) {
		this.selfArmyInfo = selfArmyInfo;
	}

	public int getChangeEnimyTimes() {
		return changeEnimyTimes;
	}

	public void setChangeEnimyTimes(int changeEnimyTimes) {
		this.changeEnimyTimes = changeEnimyTimes;
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

	public int getHurtTotal() {
		return hurtTotal;
	}

	public void setHurtTotal(int hurtTotal) {
		this.hurtTotal = hurtTotal;
	}
}
