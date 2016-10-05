package com.playerdata.groupcompetition.dao.pojo;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.groupcompetition.holder.data.GCompTeam;

public class GCompMatchTeamData {

	private Map<Integer, GCompGroupTeamMgr> _allGroupTeamHolders = new ConcurrentHashMap<Integer, GCompGroupTeamMgr>(); // key=matchId，value=帮派的队伍数据
	
	public GCompGroupTeamMgr getGroupTeamData(int matchId) {
		return _allGroupTeamHolders.get(matchId);
	}
	
	public void addGroupTeamHolder(int matchId, GCompGroupTeamMgr data) {
		this._allGroupTeamHolders.put(matchId, data);
	}
	
	public void clear() {
		this._allGroupTeamHolders.clear();
	}
	
	public List<GCompTeam> removeAllTeam() {
		List<GCompTeam> all = new ArrayList<GCompTeam>();
		for(Iterator<Integer> keyItr = _allGroupTeamHolders.keySet().iterator(); keyItr.hasNext();) {
			all.addAll(_allGroupTeamHolders.get(keyItr.next()).removeAll());
		}
		return all;
	}
}
