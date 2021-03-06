package com.rw.handler.groupFight.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;
import com.rw.handler.groupFight.armySimple.ArmyInfoSimple;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFDefendArmyItem implements SynItem{
	private String armyID;  // armyID = userID_teamID
	
	private String groupID;
	
	private String userID;
	
	private int teamID;
	
	private ArmyInfoSimple simpleArmy;
	
	private long lastOperateTime; // 被操作的时间，需要判断是否过期(包括选中和挑战) ::注意多线程并发问题
	
	private int state;	// 是否正在被挑战,是否被选中,以及是否阵亡 ::注意多线程并发问题<-1，没有上阵；1，正常；2，被选中；3，正在被挑战；4，阵亡>
	
	private long setDefenderTime;	// 上阵时间（用于排序）

	public String getId() {
		return armyID;
	}

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

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public ArmyInfoSimple getSimpleArmy() {
		return simpleArmy;
	}

	public void setSimpleArmy(ArmyInfoSimple simpleArmy) {
		this.simpleArmy = simpleArmy;
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

	public boolean setState(int state) {
		synchronized (GFDefendArmyItem.class) {
			if(this.state == state) return false;
			this.state = state;
			return true;
		}
	}

	public long getSetDefenderTime() {
		return setDefenderTime;
	}

	public void setSetDefenderTime(long setDefenderTime) {
		this.setDefenderTime = setDefenderTime;
	}
}
