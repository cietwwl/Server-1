package com.playerdata.teambattle.manager;

import com.bm.robot.RandomData;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;

public class TeamMatchData {

	
	private ArmyInfoSimple armySimpleInfo;
	
	private RandomData randomData;
	
	public TeamMatchData(ArmyInfoSimple armySimpleInfoP, RandomData randomDataP){
		
		this.armySimpleInfo = armySimpleInfoP;
		this.randomData = randomDataP;		
		
	}

	public ArmyInfoSimple getArmySimpleInfo() {
		return armySimpleInfo;
	}

	public RandomData getRandomData() {
		return randomData;
	}
	
	public StaticMemberTeamInfo toStaticMemberTeamInfo(){
		StaticMemberTeamInfo staticMemberTeamInfo = new StaticMemberTeamInfo();
		staticMemberTeamInfo.setUserStaticTeam(armySimpleInfo);
		staticMemberTeamInfo.setUserID(armySimpleInfo.getPlayer().getId());
		return staticMemberTeamInfo;
		
	}
	
}
