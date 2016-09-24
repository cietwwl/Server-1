package com.playerdata.groupcompetition.holder.data;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompBattleResult;

@SynClass
public class GCompTeamMember {

	private boolean isLeader;
	private ArmyInfoSimple teamInfo;

	/**
	 * 战斗的结果，默认是未开战
	 */
	private GCompBattleResult result = GCompBattleResult.NonStart;
	private boolean isReady; // 是否准备好

	public GCompTeamMember(boolean pIsLeader, ArmyInfoSimple pTeamInfo) {
		this.isLeader = pIsLeader;
		this.teamInfo = pTeamInfo;
	}

	public void setLeader(boolean value) {
		this.isLeader = value;
	}
	
	public boolean isLeader() {
		return this.isLeader;
	}

	public String getUserId() {
		return teamInfo.getPlayer().getId();
	}

	public ArmyInfoSimple getTeamInfo() {
		return teamInfo;
	}

	/**
	 * 获取战斗结果
	 * 
	 * @return
	 */
	public GCompBattleResult getResult() {
		return result;
	}

	/**
	 * 设置战斗结果
	 * 
	 * @param result
	 */
	public void setResult(GCompBattleResult result) {
		this.result = result;
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public void setReady(boolean value) {
		this.isReady = value;
	}
}