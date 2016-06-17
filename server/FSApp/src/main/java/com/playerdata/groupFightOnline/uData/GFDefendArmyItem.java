package com.playerdata.groupFightOnline.uData;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataExtend.HeroStateInfo;
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
	private List<HeroStateInfo> heros;
	
	@CombineSave
	private long lastOperateTime; // 被操作的时间，需要判断是否过期(包括选中和挑战) ::注意多线程并发问题
	
	@CombineSave
	private int state;	// 是否正在被挑战,是否被选中  ::注意多线程并发问题
	
	private int version;

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

	public List<HeroStateInfo> getHeros() {
		return heros;
	}

	public void setHeros(List<HeroStateInfo> heros) {
		this.heros = heros;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
