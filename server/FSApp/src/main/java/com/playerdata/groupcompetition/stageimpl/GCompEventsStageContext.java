package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsStageContext {

	private List<String> _groupIds;
	private List<String> _loseGroupIds; // 总决赛要用loseGroupIds
	private GCEventsType _status;
	
	public GCompEventsStageContext(List<String> groupIds, List<String> loseGroupIds, GCEventsType status) {
		this._groupIds = Collections.unmodifiableList(new ArrayList<String>(groupIds));
		this._loseGroupIds = Collections.unmodifiableList(new ArrayList<String>(loseGroupIds));
		this._status = status;
	}
	
	public List<String> getGroupIds() {
		return _groupIds;
	}
	
	public List<String> getLoseGroupIds() {
		return _loseGroupIds;
	}
	
	public GCEventsType getStatus() {
		return _status;
	}
}
