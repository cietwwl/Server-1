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
	
	private long lastOperateTime; // 被操作的时间，需要判断是否过期(包括选中和挑战) ::注意多线程并发问题
	
	private int state;	// 是否正在被挑战,是否被选中,以及是否阵亡 ::注意多线程并发问题

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
