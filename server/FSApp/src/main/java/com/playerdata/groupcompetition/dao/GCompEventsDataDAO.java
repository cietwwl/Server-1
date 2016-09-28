package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsDataDAO {

	private static final GCompEventsDataDAO _instance = new GCompEventsDataDAO();

	public static final GCompEventsDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompEventsGlobalData _currentGlobalData = new GCompEventsGlobalData();
	private final GCompEventsGlobalData _lastGlobalData = new GCompEventsGlobalData();
	
	public GCompEventsGlobalData getCurrentGlobalData() {
		return _currentGlobalData;
	}
	
	public GCompEventsGlobalData getLastGlobalData() {
		return _lastGlobalData;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this._currentGlobalData.add(eventsType, eventsData);
	}
}
