package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.dao.pojo.GCompGroupTeamMgr;
import com.playerdata.groupcompetition.dao.pojo.GCompMatchTeamData;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompTeamDataDAO {
	
	private static final GCompTeamDataDAO _instance = new GCompTeamDataDAO();
	
	public static final GCompTeamDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompMatchTeamData _matchTeamData = new GCompMatchTeamData();
	
	public void clearMatchTeamData() {
		_matchTeamData.clear();
	}
	
	public void addGroupTeamData(int matchId, GCompGroupTeamMgr groupTeamData) {
		this._matchTeamData.addGroupTeamHolder(matchId, groupTeamData);
	}
	
	/**
	 * 
	 * 增加一队team
	 * 
	 * @param matchId
	 * @param data
	 */
	public void addTeam(GCEventsType eventsType, int matchId, String groupId, GCompTeam data) {
		_matchTeamData.getGroupTeamData(matchId).addTeamData(groupId, data);
	}
	
	/**
	 * 
	 * 获取user的队伍
	 * 
	 * @param eventsType
	 * @param matchId
	 * @param userId
	 * @return
	 */
	public GCompTeam getTeamOfUser(int matchId, String userId, String groupId) {
		GCompGroupTeamMgr groupTeamData = _matchTeamData.getGroupTeamData(matchId);
		return groupTeamData.getTeamData(userId, groupId);
	}
	
	/**
	 * 
	 * @param matchId
	 * @param teamId
	 * @return
	 */
	public GCompTeam getTeamDataByTeamId(int matchId, String teamId) {
		GCompGroupTeamMgr groupTeamMgr = _matchTeamData.getGroupTeamData(matchId);
		return groupTeamMgr.getTeamData(teamId);
	}
}
