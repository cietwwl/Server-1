package com.playerdata.groupcompetition.matching;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.Player;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCompEventsStatus;

/**
 * 
 * 帮派争霸匹配中心
 * 
 * @author CHEN.P
 *
 */
public class GroupCompetitionMatchingCenter {

	private static GroupCompetitionMatchingCenter _instance = new GroupCompetitionMatchingCenter();
	
	private final Map<Integer, AgainstMatchingTask> _matchingDataMap = new ConcurrentHashMap<Integer, AgainstMatchingTask>();
	
	public static GroupCompetitionMatchingCenter getInstance() {
		return _instance;
	}
	
	public void onEventsStart(List<GCompAgainst> againsts) {
		_matchingDataMap.clear();
		for (GCompAgainst against : againsts) {
			IGCGroup groupA = against.getGroupA();
			IGCGroup groupB = against.getGroupB();
			AgainstMatchingTask task = new AgainstMatchingTask(against.getId(), groupA.getGroupId(), groupB.getGroupId());
			_matchingDataMap.put(against.getId(), task);
		}
	}
	
	public void onEventsStatusChange(GCompEventsStatus status) {
		StrategyOfEventsStatus strategy = StrategyOfEventsStatus.getByType(status);
		if (strategy != null) {
			for (Iterator<Integer> keyItr = _matchingDataMap.keySet().iterator(); keyItr.hasNext();) {
				strategy.handleStatus(_matchingDataMap.get(keyItr.next()));
			}
		}
	}
	
	/**
	 * 
	 * 匹配
	 * 
	 * @param matchId
	 * @param groupId
	 * @param team
	 */
	public void submitToMatchingCenter(int matchId, String groupId, GCompTeam team) {
		AgainstMatchingTask data = _matchingDataMap.get(matchId);
		data.addMatching(groupId, team);
	}
	
	/**
	 * 
	 * 取消匹配
	 * 
	 * @param matchId
	 * @param groupId
	 * @param team
	 */
	public boolean cancelMatching(int matchId, String groupId, GCompTeam team) {
		AgainstMatchingTask data = _matchingDataMap.get(matchId);
		return data.cancelMatching(groupId, team);
	}
	
	/**
	 * 
	 * 是否正在随机匹配
	 * 
	 * @param matchId
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean isInRandomMatching(int matchId, String groupId, String userId) {
		AgainstMatchingTask data = _matchingDataMap.get(matchId);
		return data.getGroupMatchingData(groupId).isInRandomMatching(userId);
	}
	
	/**
	 * 
	 * 添加到随机匹配中
	 * 
	 * @param matchId
	 * @param groupId
	 * @param player
	 * @param heroIds
	 */
	public void addToRandomMatching(int matchId, String groupId, Player player, List<String> heroIds) {
		AgainstMatchingTask data = _matchingDataMap.get(matchId);
		GroupMatchingData gmd = data.getGroupMatchingData(groupId);
		if(gmd.isInRandomMatching(player.getUserId())) {
			return;
		}
		RandomMatchingData rmd = RandomMatchingData.createActiveSubmitData(player.getUserId(), heroIds);
		gmd.addRandomMatchingData(rmd);
	}
	
	/**
	 * 
	 * 取消随机匹配
	 * 
	 * @param matchId
	 * @param groupId
	 * @param player
	 */
	public boolean cancelRandomMatching(int matchId, String groupId, Player player) {
		AgainstMatchingTask data = _matchingDataMap.get(matchId);
		GroupMatchingData gmd = data.getGroupMatchingData(groupId);
		return gmd.cancelRandomMatchingData(player.getUserId());
	}
}
