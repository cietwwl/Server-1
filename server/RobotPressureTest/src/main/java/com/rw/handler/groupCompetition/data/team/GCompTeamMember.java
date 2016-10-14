package com.rw.handler.groupCompetition.data.team;

import com.rw.dataSyn.SynItem;
import com.rw.handler.groupCompetition.util.GCompBattleResult;
import com.rw.handler.groupFight.armySimple.ArmyInfoSimple;

public class GCompTeamMember implements SynItem {

	private boolean isLeader;
	private String userId; // 玩家id，需要同步到客户端
	private ArmyInfoSimple armyInfo;

	/**
	 * 战斗的结果，默认是未开战
	 */
	private volatile GCompBattleResult result = GCompBattleResult.NonStart;
	private boolean isReady; // 是否准备好
	
	public GCompTeamMember() {}

	public GCompTeamMember(boolean pIsLeader, ArmyInfoSimple pTeamInfo) {
		this.isLeader = pIsLeader;
		this.armyInfo = pTeamInfo;
		this.userId = pTeamInfo.getPlayer().getId();
	}
	
	@Override
	public String getId() {
		return userId;
	}

	public void setLeader(boolean value) {
		this.isLeader = value;
	}

	public boolean isLeader() {
		return this.isLeader;
	}

	public String getUserId() {
		return armyInfo.getPlayer().getId();
	}

	public ArmyInfoSimple getArmyInfo() {
		return armyInfo;
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
