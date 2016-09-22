package com.playerdata.groupcompetition.dao.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.holder.data.GCompTeam;

public class GCompGroupTeamHolder {

	private Map<String, GCompTeam> _teamDatas = new ConcurrentHashMap<String, GCompTeam>(); // key=队伍id，value=队伍数据
	private Map<String, List<GCompTeam>> _teamDatasByGroup = new ConcurrentHashMap<String, List<GCompTeam>>(4, 1.0f); // key=帮派id，value=包含的队伍数据
	private Map<String, String> _userIdOfTeam = new ConcurrentHashMap<String, String>(); // key=userId，value=队伍的id
	
	public GCompGroupTeamHolder(String idOfGroupA, String idOfGroupB) {
		_teamDatasByGroup.put(idOfGroupA, new ArrayList<GCompTeam>());
		_teamDatasByGroup.put(idOfGroupB, new ArrayList<GCompTeam>());
	}
	
	public void addTeamData(String groupId, GCompTeam teamData) {
		this._teamDatas.put(teamData.getId(), teamData);
		this._teamDatasByGroup.get(groupId).add(teamData);
		List<GCompTeamMember> list = teamData.getMembers();
		String teamId = teamData.getId();
		for(int i = 0, size = list.size(); i < size; i++) {
			GCompTeamMember temp = list.get(i);
			_userIdOfTeam.put(temp.getUserId(), teamId);
		}
	}
	
	public GCompTeam getTeamData(String userId) {
		String teamId = _userIdOfTeam.get(userId);
		if(teamId != null) {
		return _teamDatas.get(teamId);
		}
		return null;
	}
}
