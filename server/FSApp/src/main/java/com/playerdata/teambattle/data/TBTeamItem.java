package com.playerdata.teambattle.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.teambattle.bm.TeamBattleConst;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.playerdata.teambattle.enums.TBMemberState;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "tb_team_item")
public class TBTeamItem implements IMapItem{
	@Id
	private String teamID;  // teamID = hardID_UUID
	
	private String hardID;
	
	@CombineSave
	private List<TeamMember> members;
	
	@CombineSave
	private String leaderID;
	
	@CombineSave
	private boolean canFreeJion = true;
	
	@NonSave
	private List<StaticMemberTeamInfo> teamMembers = new ArrayList<StaticMemberTeamInfo>();
	
	@NonSave
	@IgnoreSynField
	private boolean isSelecting = false;
	
	@Override
	public String getId() {
		return teamID;
	}
	
	public TBTeamItem(){
		members = new ArrayList<TeamMember>();
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

	public String getLeaderID() {
		return leaderID;
	}

	public void setLeaderID(String leaderID) {
		this.leaderID = leaderID;
	}
	
	public synchronized List<TeamMember> getMembers(){
		return new ArrayList<TeamMember>(members);
	}
	
	public synchronized TeamMember findMember(String userID){
		for(TeamMember mem : members){
			if(StringUtils.equals(mem.getUserID(), userID)) return mem;
		}
		return null;
	}
	
	public synchronized boolean needRefreshTeamMembers(){
		return teamMembers == null || teamMembers.isEmpty();
	}
	
	public synchronized boolean isFull(){
		if(members.size() >= TeamBattleConst.TEAM_MAX_MEMBER) return true;
		if(members.size() == TeamBattleConst.TEAM_MAX_MEMBER - 1) return isSelecting;
		return false;
	}
	
	public boolean addMember(TeamMember member){
		if(members.size() >= TeamBattleConst.TEAM_MAX_MEMBER) return false;
		members.add(member);
		teamMembers = null;
		return true;
	}
	
	public boolean removeMember(TeamMember member){
		boolean result = members.remove(member);
		if(result){
			StaticMemberTeamInfo staticMem = findTeamMember(member.getUserID());
			if(null != staticMem) teamMembers.remove(staticMem);
		}
		if(members.size() == 0) return true;
		if(StringUtils.equals(member.getUserID(), leaderID)){
			leaderID = members.get(0).getUserID();
		}
		return result;
	}
	
	public synchronized void changeLeaderAfterFinish(String userID){
		if(StringUtils.equals(userID, leaderID) && members.size() >= 2){
			TeamMember member = findMember(userID);
			if(null == member) return;
			for(TeamMember mem : members){
				if(mem.getState().equals(TBMemberState.Ready) 
						|| mem.getState().equals(TBMemberState.Fight) 
						|| mem.getState().equals(TBMemberState.HalfFinish)){
					leaderID = mem.getUserID();
				}
			}
		}
	}

	public boolean isCanFreeJion() {
		return canFreeJion;
	}

	public void setCanFreeJion(boolean canFreeJion) {
		this.canFreeJion = canFreeJion;
	}

	public List<StaticMemberTeamInfo> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<StaticMemberTeamInfo> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public boolean isSelecting() {
		return isSelecting;
	}

	public void setSelecting(boolean isSelecting) {
		this.isSelecting = isSelecting;
	}
	
	public boolean removeAble(){
		if(members.size() == 0) return true;
		if(members.size() < TeamBattleConst.TEAM_MAX_MEMBER) return false;
		for(TeamMember member : members){
			if(member.getState().equals(TBMemberState.Fight)) return false;
			if(member.getState().equals(TBMemberState.HalfFinish)) return false;
			if(member.getState().equals(TBMemberState.Ready)) return false;
		}
		return true;
	}
	
	private StaticMemberTeamInfo findTeamMember(String userID){
		if(null == teamMembers) return null;
		for(StaticMemberTeamInfo mem : teamMembers){
			if(StringUtils.equals(mem.getUserID(), userID)) return mem;
		}
		return null;
	}
}
