package com.bm.rank.groupCompetition.groupRank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCompFightingItem {

	private String groupId;
	
	private String groupName;
	
	private int groupLevel;
	
	private String groupIcon;
	
	private String leaderName;

	private long groupFight;
	
	private int lastRank;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	public String getGroupIcon() {
		return groupIcon;
	}

	public void setGroupIcon(String groupIcon) {
		this.groupIcon = groupIcon;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public long getGroupFight() {
		return groupFight;
	}

	public void setGroupFight(long groupFight) {
		this.groupFight = groupFight;
	}

	public int getLastRank() {
		return lastRank;
	}

	public void setLastRank(int lastRank) {
		this.lastRank = lastRank;
	}
}
