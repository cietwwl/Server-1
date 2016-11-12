package com.rw.handler.groupFight.dataForClient;


/**
 * 防守队伍的基本信息
 * 用来返回给客户端，锁定的要挑战的队伍
 * @author aken
 */
public class DefendArmySimpleInfo {
	private String groupID;		//敌方防守队伍所属于的帮派
	private String defendArmyID;	//防守队伍的id
	private long lockArmyTime;	//选中或战斗锁定队伍的时间
	
	public String getGroupID() {
		return groupID;
	}
	
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	
	public String getDefendArmyID() {
		return defendArmyID;
	}
	
	public void setDefendArmyID(String defendArmyID) {
		this.defendArmyID = defendArmyID;
	}

	public long getLockArmyTime() {
		return lockArmyTime;
	}

	public void setLockArmyTime(long lockArmyTime) {
		this.lockArmyTime = lockArmyTime;
	}
}
