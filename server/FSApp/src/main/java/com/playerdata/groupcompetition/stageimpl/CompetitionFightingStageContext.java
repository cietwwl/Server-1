package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.List;

public class CompetitionFightingStageContext {

	private List<String> _groupIds;
	private CompetitionEventsStatus _status;
	
	public CompetitionFightingStageContext(List<String> groupIds, CompetitionEventsStatus status) {
		this._groupIds = new ArrayList<String>(groupIds);
		this._status = status;
	}
	
	public List<String> getGroupIds() {
		return _groupIds;
	}
	
	public CompetitionEventsStatus getStatus() {
		return _status;
	}
}
