package com.common.serverdata;

import java.util.HashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
}
