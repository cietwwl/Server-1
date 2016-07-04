package com.groupCopy.rwbase.dao.groupCopy.db;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class GroupCopyArmyDamageInfo {

	
	private String playerID;
	private GroupCopyTeamInfo army;
	//击杀时间
	private long time;
	
	private int damage;
	
	
	public GroupCopyArmyDamageInfo() {
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	public void setPlayerID(String id) {
		this.playerID = id;
	}
	public void setTime(long killTime) {
		this.time = killTime;
	}
	public void setArmy(GroupCopyTeamInfo teamInfo) {
		army = teamInfo;
	}
	public String getGuildName() {
		return army.getGuildName();
	}
	
	public String getPlayerID() {
		return playerID;
	}
	public GroupCopyTeamInfo getArmy() {
		return army;
	}
	public long getTime() {
		return time;
	}
	public int getDamage() {
		return damage;
	}

}
