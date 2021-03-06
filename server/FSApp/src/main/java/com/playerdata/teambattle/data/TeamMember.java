package com.playerdata.teambattle.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.robot.RandomData;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.teambattle.enums.TBMemberState;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamMember {
	
	private String userID;
	
	private int lastFinishBattle = 0;
	
	private TBMemberState state;
	
	@IgnoreSynField
	private long fightStartTime = 0;
	
	@IgnoreSynField
	private String userName;
	
	private boolean isRobot = false;
	
	@IgnoreSynField
	private RandomData randomData;

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

	public boolean isRobot() {
		return isRobot;
	}

	public void setRobot(boolean isRobot) {
		this.isRobot = isRobot;
	}

	public RandomData getRandomData() {
		return randomData;
	}

	public void setRandomData(RandomData randomData) {
		this.randomData = randomData;
	}
	
}
