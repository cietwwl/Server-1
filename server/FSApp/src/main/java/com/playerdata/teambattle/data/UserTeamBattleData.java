package com.playerdata.teambattle.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTeamBattleData {

	@Id
	private String id;
	
	@CombineSave
	private String teamID;
	
	@CombineSave
	private String memPos;	//为前端保存成员上阵顺序
	
	@CombineSave
	private int score;

	@CombineSave
	private int tbGold; // 组队战货币
	
	@CombineSave
	private List<Integer> finishedLoops = new ArrayList<Integer>();	//假如一个难度（即章节）三个节点，这个是已经完成的节点id号，如果已经有完成的（并且没有全部完成），就不能更换难度（章节）
	
	@CombineSave
	private HashMap<String, TeamHardInfo> finishedHardMap = new HashMap<String, TeamHardInfo>();	//章节完成的情况
	
	@CombineSave
	@IgnoreSynField
	private StaticMemberTeamInfo selfTeamInfo;	//个人队伍信息（其它人开战时，到这里取队友的静态队伍信息）
	
	@NonSave
	private HashMap<String, String> enimyMap = new HashMap<String, String>();	//每个难度里的，怪物组（每天不同的怪物组，前端用）
	
	@NonSave
	@IgnoreSynField
	private boolean isSynTeam = false;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getTeamID() {
		return teamID;
	}

	public void setTeamID(String teamID) {
		this.teamID = teamID;
	}
	
	public String getMemPos() {
		return memPos;
	}

	public void setMemPos(String memPos) {
		this.memPos = memPos;
	}

	public List<Integer> getFinishedLoops() {
		return finishedLoops;
	}

	public void setFinishedLoops(List<Integer> finishedLoops) {
		this.finishedLoops = finishedLoops;
	}

	public StaticMemberTeamInfo getSelfTeamInfo() {
		return selfTeamInfo;
	}

	public synchronized void setSelfTeamInfo(StaticMemberTeamInfo selfTeamInfo) {
		this.selfTeamInfo = selfTeamInfo;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTbGold() {
		return tbGold;
	}

	public void setTbGold(int tbGold) {
		this.tbGold = tbGold;
	}
	
	public HashMap<String, TeamHardInfo> getFinishedHardMap() {
		return finishedHardMap;
	}

	public void setFinishedHardMap(HashMap<String, TeamHardInfo> finishedHardMap) {
		this.finishedHardMap = finishedHardMap;
	}

	public boolean isSynTeam() {
		return isSynTeam;
	}

	public void setSynTeam(boolean isSynTeam) {
		this.isSynTeam = isSynTeam;
	}

	public HashMap<String, String> getEnimyMap() {
		return enimyMap;
	}

	public void setEnimyMap(HashMap<String, String> enimyMap) {
		this.enimyMap = enimyMap;
	}

	public void clearCurrentTeam(){
		finishedLoops.clear();
		teamID = null;
	}
	
	public void dailyReset(){
		enimyMap = null;
		finishedLoops.clear();
		finishedHardMap.clear();
		List<TeamCfg> teamCfgs = TeamCfgDAO.getInstance().getAllCfg();
		for(TeamCfg cfg : teamCfgs){
			TeamHardInfo hardInfo = new TeamHardInfo();
			hardInfo.setHardID(cfg.getId());
			hardInfo.setBuyTimes(0);
			hardInfo.setFinishTimes(0);
		}
		teamID = null;
	}
}
