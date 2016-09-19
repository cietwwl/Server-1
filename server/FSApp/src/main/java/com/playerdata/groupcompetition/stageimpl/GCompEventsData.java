package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.groupcompetition.util.GCEventsStatus;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsData {

	private GCEventsType _eventsType; // 赛事类型
	private List<GCompAgainst> _againsts; // 对阵关系
	private List<GCompAgainst> _againstsRO; // 对阵关系（只读）
	private GCEventsStatus _currentStatus = GCEventsStatus.NONE; // 赛事的当前状态
	private List<String> _winGroupIds;
	private List<String> _winGroupIdsRO;
	
	void setEventsType(GCEventsType pEventsType) {
		this._eventsType = pEventsType;
	}
	
	public GCEventsType getEventsType() {
		return _eventsType;
	}
	
	void setAgainsts(List<GCompAgainst> list) {
		this._againsts = new ArrayList<GCompAgainst>(list);
		this._againstsRO = Collections.unmodifiableList(_againsts);
	}
	
	public List<GCompAgainst> getAgainsts() {
		return _againstsRO;
	}
	
	void setCurrentStatus(GCEventsStatus pStatus) {
		this._currentStatus = pStatus;
	}
	
	public GCEventsStatus getCurrentStatus() {
		return _currentStatus;
	}
	
	void setWinGroupIds(List<String> groupIds) {
		this._winGroupIds = new ArrayList<String>(groupIds);
		this._winGroupIdsRO = Collections.unmodifiableList(_winGroupIds);
	}
	
	public List<String> getWinGroupIds() {
		return _winGroupIdsRO;
	}
}
