package com.playerdata.groupcompetition.holder.data;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompBattleResult;

@SynClass
public class GCompTeamMember {

	private boolean isLeader;
	private String userId; // 玩家id，需要同步到客户端
	private ArmyInfoSimple armyInfo;
	@IgnoreSynField
	private String descr;
	@IgnoreSynField
	private boolean robot; // 是否机器人

	/**
	 * 战斗的结果，默认是未开战
	 */
	@IgnoreSynField
	private volatile GCompBattleResult result = GCompBattleResult.NonStart;
	private boolean isReady; // 是否准备好
	@IgnoreSynField
	private volatile long startBattleTime;// 开始战斗的时间
	@IgnoreSynField
	private volatile String enemyName;// 对手的名字

	public GCompTeamMember(boolean pIsLeader, ArmyInfoSimple pTeamInfo) {
		this.isLeader = pIsLeader;
		this.armyInfo = pTeamInfo;
		this.userId = pTeamInfo.getPlayer().getId();
		this.descr = this.getClass().getSimpleName() + "[userId=" + this.userId + ", heroIds=" + this.armyInfo.getHeroIdList() + "]";
	}

	public void setLeader(boolean value) {
		this.isLeader = value;
	}

	public boolean isLeader() {
		return this.isLeader;
	}

	public void setRobot(boolean robot) {
		this.robot = robot;
	}

	public boolean isRobot() {
		return this.robot;
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

	public long getStartBattleTime() {
		return startBattleTime;
	}

	public void setStartBattleTime(long startBattleTime) {
		this.startBattleTime = startBattleTime;
	}

	public String getEnemyName() {
		return enemyName;
	}

	public void setEnemyName(String enemyName) {
		this.enemyName = enemyName;
	}

	@Override
	public String toString() {
		return descr;
	}
}