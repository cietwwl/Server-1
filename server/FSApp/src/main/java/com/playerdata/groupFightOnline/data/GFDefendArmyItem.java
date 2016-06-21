package com.playerdata.groupFightOnline.data;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gf_defend_army_item")
public class GFDefendArmyItem implements IMapItem{
	@Id
	private String armyID;  // armyID = userID_teamID
	
	private String groupID;
	
	@CombineSave
	private String userID;
	
	@CombineSave
	private int teamID;
	
	@CombineSave
	private List<ArmyInfoSimple> simpleArmy;
	
	@CombineSave
	private long lastOperateTime; // 被操作的时间，需要判断是否过期(包括选中和挑战) ::注意多线程并发问题
	
	@CombineSave
	private int state;	// 是否正在被挑战,是否被选中,以及是否阵亡 ::注意多线程并发问题
	
	@CombineSave
	private long setDefenderTime;	// 上阵时间（用于排序）

	public String getId() {
		return armyID;
	}

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

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public List<ArmyInfoSimple> getSimpleArmy() {
		return simpleArmy;
	}

	public void setSimpleArmy(List<ArmyInfoSimple> simpleArmy) {
		this.simpleArmy = simpleArmy;
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

	public long getSetDefenderTime() {
		return setDefenderTime;
	}

	public void setSetDefenderTime(long setDefenderTime) {
		this.setDefenderTime = setDefenderTime;
	}
}
