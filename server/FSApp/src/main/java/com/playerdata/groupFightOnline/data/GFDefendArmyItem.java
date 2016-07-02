package com.playerdata.groupFightOnline.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataForClient.GFDefendArmySimpleLeader;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;

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
	private ArmyInfoSimple simpleArmy;
	
	@CombineSave
	private long lastOperateTime; // 被操作的时间，需要判断是否过期(包括选中和挑战) ::注意多线程并发问题
	
	@CombineSave
	private int state;	// 是否正在被挑战,是否被选中,以及是否阵亡 ::注意多线程并发问题<-1，没有上阵；1，正常；2，被选中；3，正在被挑战；4，阵亡>
	
	@CombineSave
	private long setDefenderTime;	// 上阵时间（用于排序）
	
	@IgnoreSynField
	@NonSave
	private GFDefendArmySimpleLeader simpleLeader = new GFDefendArmySimpleLeader();

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

	public ArmyInfoSimple getSimpleArmy() {
		return simpleArmy;
	}

	public void setSimpleArmy(ArmyInfoSimple simpleArmy) {
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

	public boolean setState(int state) {
		synchronized (GFDefendArmyItem.class) {
			if(this.state == state) return false;
			this.state = state;
			return true;
		}
	}

	public long getSetDefenderTime() {
		return setDefenderTime;
	}

	public void setSetDefenderTime(long setDefenderTime) {
		this.setDefenderTime = setDefenderTime;
	}
	
	public GFDefendArmySimpleLeader getSimpleLeader(){
		if(simpleArmy == null || simpleArmy.getHeroList() == null || simpleArmy.getHeroList().size() == 0) return null;
		ArmyHeroSimple heroSimple = simpleArmy.getHeroList().get(0);
		simpleLeader.setArmyID(armyID);
		simpleLeader.setGroupID(groupID);
		simpleLeader.setState(state);
		simpleLeader.setLevel(heroSimple.getLevel());
		simpleLeader.setModeId(heroSimple.getModeId());
		simpleLeader.setQualityId(heroSimple.getQualityId());
		simpleLeader.setStarLevel(heroSimple.getStarLevel());
		return simpleLeader;
	}
}
