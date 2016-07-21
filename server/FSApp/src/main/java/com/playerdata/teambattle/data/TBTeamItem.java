package com.playerdata.teambattle.data;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "tb_team_item")
public class TBTeamItem implements IMapItem{
	@Id
	private String teamID;  // armyID = userID_teamID
	
	private String hardID;
	
	@CombineSave
	private List<TeamMember> members;

	@Override
	public String getId() {
		return teamID;
	}
	
	public String getTeamID() {
		return teamID;
	}

	public void setTeamID(String teamID) {
		this.teamID = teamID;
	}

	public String getHardID() {
		return hardID;
	}

	public void setHardID(String hardID) {
		this.hardID = hardID;
	}

	public List<TeamMember> getMembers() {
		return members;
	}

	public void setMembers(List<TeamMember> members) {
		this.members = members;
	}
}
