package com.playerdata.groupcompetition.dao.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GCompMatchTeamData {

	private Map<Integer, GCompGroupTeamData> _allGroupTeamDatas = new ConcurrentHashMap<Integer, GCompGroupTeamData>(); // key=matchId，value=帮派的队伍数据
	
	public GCompGroupTeamData getGroupTeamData(int matchId) {
		return _allGroupTeamDatas.get(matchId);
	}
	
	public void addGroupTeamData(int matchId, GCompGroupTeamData data) {
		this._allGroupTeamDatas.put(matchId, data);
	}
	
	public void clear() {
		this._allGroupTeamDatas.clear();
	}
}
