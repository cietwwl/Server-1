package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsDataMgr {

	private static GCompEventsDataMgr _instance = new GCompEventsDataMgr();

	public static GCompEventsDataMgr getInstance() {
		return _instance;
	}

	private GCompEventsDataHolder _dataHolder = GCompEventsDataHolder.getInstance();

	public void onEventStageStart(GCEventsType startEventsType) {
		GCompEventsGlobalData globalData = _dataHolder.get(false);
		globalData.clear();
		globalData.setMatchNumType(startEventsType);
	}

	/**
	 * 
	 * 把对阵添加到同步数据中
	 * 
	 * @param againstList
	 * @param eventsType
	 */
	public void addEvents(GCompEventsData eventsData, GCEventsType eventsType) {
		_dataHolder.get(false).add(eventsType, eventsData);
	}

	/**
	 * 
	 * @param eventType
	 * @return
	 */
	public GCompEventsData getEventsData(GCEventsType eventType) {
		return _dataHolder.get(false).getEventsData(eventType);
	}

	/**
	 * 
	 * 发送比赛数据
	 * 
	 * @param player
	 */
	public void sendMatchData(Player player) {
		this._dataHolder.syn(player);
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
		GCompEventsData eventsData = _dataHolder.get(false).getEventsData(eventsType);
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
		GCompEventsData eventsData = _dataHolder.get(false).getEventsData(eventsType);
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
	
	public GCGroup getGCGroupOfCurrentEvents(String groupId) {
		GCEventsType type = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsData eventsData = _dataHolder.get(false).getEventsData(type);
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
	
	public void notifyAllEventsFinished() {
		this._dataHolder.saveCurrentToLast();
	}
}