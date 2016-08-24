package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCTeamDataMgr {

	private static final GCTeamDataMgr _instance = new GCTeamDataMgr();
	
	public static final GCTeamDataMgr getInstance() {
		return _instance;
	}
	
	private GCTeamDataHolder _dataHolder = GCTeamDataHolder.getInstance();
	
	protected GCTeamDataMgr() {
		
	}
	
	public void onEventsStart(GCEventsType eventsType) {
		this._dataHolder.clearTeamData();
	}
	
	public void sendTeamData(int matchId, Player player) {
		this._dataHolder.syn(matchId, player);
	}
	
	/**
	 * 
	 * 创建队伍
	 * 
	 * @param player
	 * @param teamData
	 */
	public void createTeam(Player player, int matchId, List<String> heroIds) {
		
	}
	
	/**
	 * 
	 * 更新自身的英雄列表
	 * 
	 * @param player
	 * @param newHeroIds
	 */
	public void updateHeros(Player player, int matchId, List<String> newHeroIds) {
		
	}
	
	/**
	 * 
	 * 加入队伍
	 * 
	 * @param player
	 * @param teamId
	 */
	public void joinTeam(Player player, int matchId, int teamId) {
		
	}
}
