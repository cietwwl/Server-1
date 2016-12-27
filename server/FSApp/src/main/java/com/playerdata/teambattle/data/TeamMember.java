package com.playerdata.teambattle.data;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.teambattle.enums.TBMemberState;

@SynClass
public class TeamMember {
	
	private String userID;
	
	private int lastFinishBattle = 0;
	
	private TBMemberState state;
	
	@IgnoreSynField
	private long fightStartTime = 0;
	
	@IgnoreSynField
	private String userName;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public TBMemberState getState() {
		return state;
	}

	public void setState(TBMemberState state) {
		this.state = state;
	}

	public int getLastFinishBattle() {
		return lastFinishBattle;
	}

	public void setLastFinishBattle(int lastFinishBattle) {
		this.lastFinishBattle = lastFinishBattle;
	}

	public long getFightStartTime() {
		return fightStartTime;
	}

	public void setFightStartTime(long fightStartTime) {
		this.fightStartTime = fightStartTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
