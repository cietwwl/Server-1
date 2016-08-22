package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.groupcompetition.util.GCEventsStatus;

public class GCEventsStageContext {

	private List<String> _groupIds;
	private GCEventsStatus _status;
	
	public GCEventsStageContext(List<String> groupIds, GCEventsStatus status) {
		this._groupIds = new ArrayList<String>(groupIds);
		this._status = status;
	}
	
	public List<String> getGroupIds() {
		return _groupIds;
	}
	
	public GCEventsStatus getStatus() {
		return _status;
	}
}
