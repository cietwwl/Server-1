package com.playerdata.groupFightOnline.dataExtend;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class DefendArmySimpleInfo {
	private String groupID;
	private String defendArmyID;
	
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
}
