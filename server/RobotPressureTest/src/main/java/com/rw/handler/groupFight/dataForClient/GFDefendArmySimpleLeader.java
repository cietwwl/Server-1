package com.rw.handler.groupFight.dataForClient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

/**
 * 请求查看所有防守队伍信息的时候，返回给客户端一个最强的人物组成的列表
 * 按页请求
 * @author aken
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFDefendArmySimpleLeader implements SynItem{
	private String armyID;  // armyID = userID_teamID
	
	private String groupID;
	
	private int modeId;	//英雄模型Id
	
	private int starLevel;	//星级
	
	private String qualityId;	//品阶Id
	
	private int level;	//等级
	
	private int state;

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

	public int getModeId() {
		return modeId;
	}

	public void setModeId(int modeId) {
		this.modeId = modeId;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	}

	public String getQualityId() {
		return qualityId;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String getId() {
		return armyID;
	}
}
