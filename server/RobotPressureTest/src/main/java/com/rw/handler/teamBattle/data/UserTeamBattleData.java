package com.rw.handler.teamBattle.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.dataSyn.SynItem;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTeamBattleData implements SynItem{

	private String id;
	
	private String teamID;
	
	private String memPos;	//为前端保存成员上阵顺序
	
	private int score;

	private int tbGold; // 组队战货币
	
	private List<Integer> finishedLoops = new ArrayList<Integer>();	//假如一个难度（即章节）三个节点，这个是已经完成的节点id号，如果已经有完成的（并且没有全部完成），就不能更换难度（章节）
	
	private List<String> finishedHards = new ArrayList<String>();	//已经完成的章节

	private HashMap<String, String> enimyMap = new HashMap<String, String>();	//每个难度里的，怪物组（每天不同的怪物组，前端用）
	
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
		finishedHards.clear();
		teamID = null;
	}
}
