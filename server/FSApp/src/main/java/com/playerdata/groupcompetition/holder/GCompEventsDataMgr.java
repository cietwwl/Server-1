package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.dao.GCompHistoryDataDAO;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.holder.data.GCompEventsSynData;
import com.playerdata.groupcompetition.holder.data.GCompHistoryData;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;

public class GCompEventsDataMgr {

	private static GCompEventsDataMgr _instance = new GCompEventsDataMgr();

	public static GCompEventsDataMgr getInstance() {
		return _instance;
	}

	private GCompEventsDataHolder _dataHolder = GCompEventsDataHolder.getInstance();

	private GCompEventsSynData createSynData() {
		GCompEventsGlobalData globalData = this._dataHolder.get();
		IReadOnlyPair<Long, Long> timeInfo = GroupCompetitionMgr.getInstance().getCurrentSessionTimeInfo();
		List<GCompAgainst> next = globalData.getNextMatches();
		GCompEventsSynData synData = new GCompEventsSynData();
		synData.addMatches(globalData.getMatches());
		if (next != null && next.size() > 0) {
			synData.addMatches(next);
		}
		synData.setMatchNumType(globalData.getMatchNumType());
		synData.setStartTime(timeInfo.getT1());
		synData.setEndTime(timeInfo.getT2());
		synData.setSession(GroupCompetitionMgr.getInstance().getCurrentSessionId());
		return synData;
	}
	
	public void onEventStageStart(GCEventsType startEventsType) {
		GCompEventsGlobalData globalData = _dataHolder.get();
		globalData.clear();
		globalData.setMatchNumType(startEventsType);
	}
	
	public void loadEventsGlobalData() {
		_dataHolder.loadEventsGlobalData();
	}

	/**
	 * 
	 * 把对阵添加到同步数据中
	 * 
	 * @param againstList
	 * @param eventsType
	 */
	public void addEvents(GCompEventsData eventsData, GCEventsType eventsType) {
		GCompEventsGlobalData globalData = _dataHolder.get();
		globalData.add(eventsType, eventsData);
		_dataHolder.update();
	}
	
	/**
	 * 
	 * @param list
	 */
	public void setNextMatches(List<GCompAgainst> list) {
		GCompEventsGlobalData globalData = _dataHolder.get();
		globalData.setNextMatches(list);
	}

	/**
	 * 
	 * @param eventType
	 * @return
	 */
	public GCompEventsData getEventsData(GCEventsType eventType) {
		return _dataHolder.get().getEventsData(eventType);
	}
	
	/**
	 * 保存数据
	 */
	public void save() {
		this._dataHolder.update();
	}

	/**
	 * 
	 * 发送比赛数据
	 * 
	 * @param player
	 */
	public void sendMatchData(Player player) {
		GCompEventsSynData synData = this.createSynData();
		String groupId = GroupHelper.getGroupId(player);
		if (groupId != null) {
			int matchId = getGroupMatchIdOfCurrent(groupId);
			synData.setMatchId(matchId);
		}
		this._dataHolder.syn(player, synData);
	}

	public int getGroupMatchIdOfCurrent(String groupId) {
		return this.getMatchIdOfGroup(groupId, GroupCompetitionMgr.getInstance().getCurrentEventsType());
	}
	/**
	 * 
	 * 获取比赛的id
	 * 
	 * @param groupId
	 * @param eventsType
	 * @return
	 */
	public int getMatchIdOfGroup(String groupId, GCEventsType eventsType) {
		GCompEventsData eventsData = _dataHolder.get().getEventsData(eventsType);
		List<GCompAgainst> againsts = eventsData.getAgainsts();
		GCompAgainst against;
		for (int i = 0, size = againsts.size(); i < size; i++) {
			against = againsts.get(i);
			if (against.getGroupA().getGroupId().equals(groupId) || against.getGroupB().getGroupId().equals(groupId)) {
				return against.getId();
			}
		}
		return 0;
	}

	/**
	 * 
	 * 获取自己帮派参与的对垒信息
	 * 
	 * @param groupId
	 * @param eventsType
	 * @return
	 */
	public IGCAgainst getGCAgainstOfGroup(String groupId, GCEventsType eventsType) {
		GCompEventsData eventsData = _dataHolder.get().getEventsData(eventsType);
		List<GCompAgainst> againsts = eventsData.getAgainsts();
		GCompAgainst against;
		for (int i = 0, size = againsts.size(); i < size; i++) {
			against = againsts.get(i);
			if (against.getGroupA().getGroupId().equals(groupId) || against.getGroupB().getGroupId().equals(groupId)) {
				return against;
			}
		}

		return null;
	}
	
	public boolean isEnemyEmpty(String groupId, GCEventsType eventsType) {
		IGCAgainst agaist = this.getGCAgainstOfGroup(groupId, eventsType);
		if (agaist == null) {
			return true;
		} else {
			return agaist.getGroupA().getGroupId().length() == 0 || agaist.getGroupB().getGroupId().length() == 0;
		}
	}
	
	public GCGroup getGCGroupOfCurrentEvents(String groupId) {
		GCEventsType type = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsData eventsData = _dataHolder.get().getEventsData(type);
		List<GCompAgainst> againsts = eventsData.getAgainsts();
		GCompAgainst against;
		for (int i = 0, size = againsts.size(); i < size; i++) {
			against = againsts.get(i);
			if (against.getGroupA().getGroupId().equals(groupId)) {
				return against.getGroupA();
			} else if (against.getGroupB().getGroupId().equals(groupId)) {
				return against.getGroupB();
			}
		}

		return null;
	}
	
	public void notifyGroupInfoChange(final Group group) {
		List<GCompAgainst> list = _dataHolder.get().getMatches();
		GCompUtil.updateGroupInfo(list, group);
		_dataHolder.update();
	}
	
	public List<GCompAgainst> getAllAgainsts() {
		return new ArrayList<GCompAgainst>(_dataHolder.get().getMatches());
	}
	
	public void notifyAllEventsFinished() {
		GCompHistoryData historyData = GCompHistoryDataDAO.getInstance().get();
		IReadOnlyPair<Long, Long> pair = GroupCompetitionMgr.getInstance().getCurrentSessionTimeInfo();
		historyData.copy(this._dataHolder.get(), pair.getT1(), pair.getT2());
		GCompHistoryDataDAO.getInstance().update();
	}
}