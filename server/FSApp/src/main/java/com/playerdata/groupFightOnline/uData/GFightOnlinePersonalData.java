package com.playerdata.groupFightOnline.uData;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataExtend.DefendArmySimpleInfo;
import com.playerdata.groupFightOnline.dataExtend.GFOnlineSelfArmyInfo;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlinePersonalData {

	@Id
	private String id;
	
	private GFOnlineSelfArmyInfo selfArmyInfo;
	
	private int changeEnimyTimes;
	
	private DefendArmySimpleInfo randomDefender;
	
	private int version;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public GFOnlineSelfArmyInfo getSelfArmyInfo() {
		return selfArmyInfo;
	}

	public void setSelfArmyInfo(GFOnlineSelfArmyInfo selfArmyInfo) {
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
