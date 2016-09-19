package com.playerdata.groupcompetition.dao.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.groupcompetition.holder.data.GCompTeamMemberSynData;
import com.playerdata.groupcompetition.holder.data.GCompTeamSynData;

public class GCompGroupTeamData {

	private Map<String, GCompTeamSynData> _teamDatas = new ConcurrentHashMap<String, GCompTeamSynData>(); // key=队伍id，value=队伍数据
	private Map<String, List<GCompTeamSynData>> _teamDatasByGroup = new ConcurrentHashMap<String, List<GCompTeamSynData>>(4, 1.0f); // key=帮派id，value=包含的队伍数据
	private Map<String, String> _userIdOfTeam = new ConcurrentHashMap<String, String>(); // key=userId，value=队伍的id
	
	public GCompGroupTeamData(String idOfGroupA, String idOfGroupB) {
		_teamDatasByGroup.put(idOfGroupA, new ArrayList<GCompTeamSynData>());
		_teamDatasByGroup.put(idOfGroupB, new ArrayList<GCompTeamSynData>());
	}
	
	public void addTeamData(String groupId, GCompTeamSynData teamData) {
		this._teamDatas.put(teamData.getId(), teamData);
		this._teamDatasByGroup.get(groupId).add(teamData);
		List<GCompTeamMemberSynData> list = teamData.getMembers();
		String teamId = teamData.getId();
		for(int i = 0, size = list.size(); i < size; i++) {
			GCompTeamMemberSynData temp = list.get(i);
			_userIdOfTeam.put(temp.getUserId(), teamId);
		}
	}
	
	public GCompTeamSynData getTeamData(String userId) {
		String teamId = _userIdOfTeam.get(userId);
		return _teamDatas.get(teamId);
	}
}
