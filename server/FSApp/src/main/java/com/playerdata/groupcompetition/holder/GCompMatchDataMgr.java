package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompMatchSynData;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompMatchDataMgr {
	
	private static GCompMatchDataMgr _instance = new GCompMatchDataMgr();
	
	public static GCompMatchDataMgr getInstance() {
		return _instance;
	}
	
	private GCompMatchDataHolder _dataHolder = GCompMatchDataHolder.getInstance();
	
	public void onEventStageStart(GCEventsType type) {
		GCompMatchSynData synData = _dataHolder.get();
		synData.clear();
		synData.setMatchNumType(type);
	}
	
	/**
	 * 
	 * 把对阵添加到同步数据中
	 * 
	 * @param againstList
	 * @param eventsType
	 */
	public void addEvents(GCompEventsData eventsData, GCEventsType eventsType) {
		_dataHolder.get().add(eventsType, eventsData);
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
	 * 
	 * 发送比赛数据
	 * 
	 * @param player
	 */
	public void sendMatchData(Player player) {
		this._dataHolder.syn(player);
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
			if (against.getGroupA().getGroupId().equals(groupId) || against.getGroupB().equals(groupId)) {
				return against.getId();
			}
		}
		return 0;
	}
}
