package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;

public class GCompTeamMgr {

	private static final GCompTeamMgr _instance = new GCompTeamMgr();
	
	public static final GCompTeamMgr getInstance() {
		return _instance;
	}
	
	private GCTeamDataHolder _dataHolder = GCTeamDataHolder.getInstance();
	
	protected GCompTeamMgr() {
		
	}
	
	private int getMatchIdOfGroup(String groupId) {
		GCEventsType currentEventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		return GCompMatchDataMgr.getInstance().getMatchIdOfGroup(groupId, currentEventsType);
	}
	
	public void onEventsStart(GCEventsType eventsType, List<? extends IGCAgainst> againsts) {
		this._dataHolder.clearTeamData();
		this._dataHolder.createTeamData(againsts);
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
	public IReadOnlyPair<Boolean, String> createTeam(Player player, List<String> heroIds) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		if (heroIds.isEmpty()) {
			result.setT2(GCompTips.getTipsHeroCountInvalidate());
			return result;
		}
		
		String groupId = GroupHelper.getGroupId(player);
		if (groupId == null) {
			result.setT2(GCompTips.getTipsYouAreNotInGroup());
			return result;
		}
		
		int matchId = this.getMatchIdOfGroup(groupId);
		if (matchId == 0) {
			result.setT2(GCompTips.getTipsYourGroupNotInMatch(GroupCompetitionMgr.getInstance().getCurrentEventsType().chineseName));
			return result;
		}
		
		return result;
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
