package com.groupCopy.rwbase.dao.groupCopy.db;


public class GroupCopyArmyDamageInfo {

	
	private String playerID;
	private GroupCopyTeamInfo army;
	//击杀时间
	private long time;
	
	private int damage;
	
	private String guildName;
	public void setDamage(int damage) {
		
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
		return guildName;
	}
	public void setGuildName(String guildName) {
		this.guildName = guildName;
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
