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
	private int score;

	@CombineSave
	private int tbGold; // 组队战货币
	
	@CombineSave
	private List<Integer> finishedLoops = new ArrayList<Integer>();	//假如一个难度（即章节）三个节点，这个是已经完成的节点id号，如果已经有完成的（并且没有全部完成），就不能更换难度（章节）
	
	@CombineSave
	private List<String> finishedHards = new ArrayList<String>();	//已经完成的章节
	
	@CombineSave
	@IgnoreSynField
	private StaticMemberTeamInfo selfTeamInfo;	//个人队伍信息（其它人开战时，到这里取队友的静态队伍信息）
	
	@CombineSave
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

	public List<Integer> getFinishedLoops() {
		return finishedLoops;
	}

	public void setFinishedLoops(List<Integer> finishedLoops) {
		this.finishedLoops = finishedLoops;
	}

	public StaticMemberTeamInfo getSelfTeamInfo() {
		return selfTeamInfo;
	}

	public void setSelfTeamInfo(StaticMemberTeamInfo selfTeamInfo) {
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

	public List<String> getFinishedHards() {
		return finishedHards;
	}

	public void setFinishedHards(List<String> finishedHards) {
		this.finishedHards = finishedHards;
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
		enimyMap.clear();
		finishedLoops.clear();
		finishedHards.clear();
		teamID = null;
		for(TeamCfg cfg : TeamCfgDAO.getInstance().getAllCfg()){
			int index = (int)(Math.random() * cfg.getListOfHero().length);
			enimyMap.put(cfg.getId(), cfg.getListOfHero()[index]);
		}
	}
}
