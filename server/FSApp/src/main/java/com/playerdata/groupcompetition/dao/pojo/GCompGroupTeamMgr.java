package com.playerdata.groupcompetition.dao.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;

public class GCompGroupTeamMgr {

	private Map<String, GCompTeam> _teamDatas = new ConcurrentHashMap<String, GCompTeam>(); // key=队伍id，value=队伍数据
	private Map<String, List<GCompTeam>> _teamDatasByGroup = new ConcurrentHashMap<String, List<GCompTeam>>(4, 1.0f); // key=帮派id，value=包含的队伍数据
	
	public GCompGroupTeamMgr(String idOfGroupA, String idOfGroupB) {
		_teamDatasByGroup.put(idOfGroupA, new ArrayList<GCompTeam>());
		_teamDatasByGroup.put(idOfGroupB, new ArrayList<GCompTeam>());
	}
	
	public void addTeamData(String groupId, GCompTeam teamData) {
		this._teamDatas.put(teamData.getTeamId(), teamData);
		this._teamDatasByGroup.get(groupId).add(teamData);
	}
	
	public GCompTeam getTeamData(String teamId) {
		return _teamDatas.get(teamId);
	}
	
	
	public GCompTeam getTeamData(String userId, String groupId) {
		List<GCompTeam> teams = _teamDatasByGroup.get(groupId);
		if (teams != null) {
			for (GCompTeam team : teams) {
				List<GCompTeamMember> members = team.getMembers();
				for (GCompTeamMember member : members) {
					if (member.getUserId().equals(userId)) {
						return team;
					}
				}
			}
		}
		return null;
	}
}
