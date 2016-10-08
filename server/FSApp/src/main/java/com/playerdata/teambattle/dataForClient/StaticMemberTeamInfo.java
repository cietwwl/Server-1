package com.playerdata.teambattle.dataForClient;

import java.util.Map;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class StaticMemberTeamInfo {
	
	private String userID;
	
	@IgnoreSynField
	private Map<String, Integer> heroPosMap;
	
	private ArmyInfoSimple userStaticTeam;	//此结构待定，前端可以识别的任意结构，服务端从玩家的英雄和法宝数据中生成，可能会有一个统一的结构

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public ArmyInfoSimple getUserStaticTeam() {
		return userStaticTeam;
	}

	public void setUserStaticTeam(ArmyInfoSimple userStaticTeam) {
		this.userStaticTeam = userStaticTeam;
	}

	public Map<String, Integer> getHeroPosMap() {
		return heroPosMap;
	}

	public void setHeroPosMap(Map<String, Integer> heroPosMap) {
		this.heroPosMap = heroPosMap;
	}
}
