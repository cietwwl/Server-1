package com.playerdata.groupFightOnline.dataExtend;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFDefendArmySimpleLeader{
	@Id
	private String armyID;  // armyID = userID_teamID
	
	private String groupID;
	
	private ArmyHeroSimple leaderHero;
	
	private long lastOperateTime; 
	
	private int state;	

	public String getArmyID() {
		return armyID;
	}

	public void setArmyID(String armyID) {
		this.armyID = armyID;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public ArmyHeroSimple getLeaderHero() {
		return leaderHero;
	}

	public void setLeaderHero(ArmyHeroSimple leaderHero) {
		this.leaderHero = leaderHero;
	}

	public long getLastOperateTime() {
		return lastOperateTime;
	}

	public void setLastOperateTime(long lastOperateTime) {
		this.lastOperateTime = lastOperateTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
