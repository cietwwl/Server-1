package com.playerdata.groupFightOnline.dataExtend;

import java.util.List;

import com.playerdata.army.CurAttrData;

public class GFightResult {
	private String groupID;
	private String defendArmyID;
	private List<CurAttrData> defenderState;	// 防守队伍状态信息
	private List<CurAttrData> selfArmyState; 	//自己队伍状态
	
	private int state;  //0-进攻失败,1-进攻胜利

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

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

	public List<CurAttrData> getDefenderState() {
		return defenderState;
	}

	public void setDefenderState(List<CurAttrData> defenderState) {
		this.defenderState = defenderState;
	}

	public List<CurAttrData> getSelfArmyState() {
		return selfArmyState;
	}

	public void setSelfArmyState(List<CurAttrData> selfArmyState) {
		this.selfArmyState = selfArmyState;
	}
}
