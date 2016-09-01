package com.playerdata.groupcompetition.dao.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GCompMatchTeamData {

	private Map<Integer, GCompGroupTeamHolder> _allGroupTeamHolders = new ConcurrentHashMap<Integer, GCompGroupTeamHolder>(); // key=matchId，value=帮派的队伍数据
	
	public GCompGroupTeamHolder getGroupTeamData(int matchId) {
		return _allGroupTeamHolders.get(matchId);
	}
	
	public void addGroupTeamHolder(int matchId, GCompGroupTeamHolder data) {
		this._allGroupTeamHolders.put(matchId, data);
	}
	
	public void clear() {
		this._allGroupTeamHolders.clear();
	}
}
