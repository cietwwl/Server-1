package com.playerdata.groupFightOnline.dataForClient;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 防守队伍的基本信息
 * 用来返回给客户端，锁定的要挑战的队伍
 * @author aken
 */
@SynClass
public class DefendArmySimpleInfo {
	private String groupID;
	private String defendArmyID;
	private ArmyInfoSimple simpleArmy;
	
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

	public ArmyInfoSimple getSimpleArmy() {
		return simpleArmy;
	}

	public void setSimpleArmy(ArmyInfoSimple simpleArmy) {
		this.simpleArmy = simpleArmy;
	}
}
