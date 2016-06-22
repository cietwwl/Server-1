package com.playerdata.groupFightOnline.dataExtend;

import java.util.List;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class DefendArmySimpleInfo {
	private String groupID;
	private String defendArmyID;
	private List<ArmyInfoSimple> simpleArmy;
	
	public String getGroupID() {
		return groupID;
	}
	
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	
	public String getDefendArmyID() {
		return defendArmyID;
	}
	
	public void setDefendArmyID(String defendArmyID) {
		this.defendArmyID = defendArmyID;
	}

	public List<ArmyInfoSimple> getSimpleArmy() {
		return simpleArmy;
	}

	public void setSimpleArmy(List<ArmyInfoSimple> simpleArmy) {
		this.simpleArmy = simpleArmy;
	}
}
