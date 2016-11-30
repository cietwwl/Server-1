package com.common.serverdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.fortuneCatType.ActivityFortuneCatRecord;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroRankRecord;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.rw.fsutil.dao.annotation.CombineSave;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "server_common_data")
public class ServerCommonData {

	@Id
	private String id;
	
	@CombineSave
	private HashMap<String, String> teamBattleEnimyMap = new HashMap<String, String>();	//每个难度里的，怪物组（每天不同的怪物组，前端用）
	
	@CombineSave
	private long tbLastRefreshTime = 0;		//组队战上次刷新时间
	
	@CombineSave
	private long msLastRefreshTime = 0;		//法宝秘境上次刷新时间
	
	@CombineSave
	private long gfLastRefreshTime = 0;		//帮战上次刷新时间
	
	@CombineSave
	private LinkedList<ActivityFortuneCatRecord> activityFortuneCatRecord = new LinkedList<ActivityFortuneCatRecord>();	//记录最近的三个摇奖
	
	//临时存一下，稍后分割出去
	@CombineSave
	private List<ActivityLimitHeroRankRecord> activityLimitHeroRankRecord = new ArrayList<ActivityLimitHeroRankRecord>();
//	private TreeMap<Integer, ActivityLimitHeroRankRecord> activityLimitHeroRankRecord = new TreeMap<Integer, ActivityLimitHeroRankRecord>();	//记录最近的三个摇奖
		
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public long getTbLastRefreshTime() {
		return tbLastRefreshTime;
	}

	public void setTbLastRefreshTime(long tbLastRefreshTime) {
		this.tbLastRefreshTime = tbLastRefreshTime;
	}

	public long getMsLastRefreshTime() {
		return msLastRefreshTime;
	}

	public void setMsLastRefreshTime(long msLastRefreshTime) {
		this.msLastRefreshTime = msLastRefreshTime;
	}

	public long getGfLastRefreshTime() {
		return gfLastRefreshTime;
	}

	public void setGfLastRefreshTime(long gfLastRefreshTime) {
		this.gfLastRefreshTime = gfLastRefreshTime;
	}

	public HashMap<String, String> getTeamBattleEnimyMap() {
		return teamBattleEnimyMap;
	}

	public void setTeamBattleEnimyMap(HashMap<String, String> teamBattleEnimyMap) {
		this.teamBattleEnimyMap = teamBattleEnimyMap;
	}	
	
	public void teamBattleDailyReset(){
		teamBattleEnimyMap.clear();
		for(TeamCfg cfg : TeamCfgDAO.getInstance().getAllCfg()){
			int index = (int)(Math.random() * cfg.getListOfHero().length);
			teamBattleEnimyMap.put(cfg.getId(), cfg.getListOfHero()[index]);
		}
	}

	public LinkedList<ActivityFortuneCatRecord> getActivityFortuneCatRecord() {
		return activityFortuneCatRecord;
	}

	public void setActivityFortuneCatRecord(
			LinkedList<ActivityFortuneCatRecord> activityFortuneCatRecord) {
		this.activityFortuneCatRecord = activityFortuneCatRecord;
	}

	public List<ActivityLimitHeroRankRecord> getActivityLimitHeroRankRecord() {
		return activityLimitHeroRankRecord;
	}

	public void setActivityLimitHeroRankRecord(
			List<ActivityLimitHeroRankRecord> activityLimitHeroRankRecord) {
		this.activityLimitHeroRankRecord = activityLimitHeroRankRecord;
	}
}
