package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsStageContext {

	private List<String> _groupIds;
	private GCEventsType _status;
	
	public GCompEventsStageContext(List<String> groupIds, GCEventsType status) {
		this._groupIds = new ArrayList<String>(groupIds);
		this._status = status;
	}
	
	public List<String> getGroupIds() {
		return _groupIds;
	}
	
	public GCEventsType getStatus() {
		return _status;
	}
}
