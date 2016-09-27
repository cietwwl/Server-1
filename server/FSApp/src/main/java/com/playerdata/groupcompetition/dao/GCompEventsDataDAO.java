package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsDataDAO {

	private static final GCompEventsDataDAO _instance = new GCompEventsDataDAO();

	public static final GCompEventsDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompEventsGlobalData _synData = new GCompEventsGlobalData();
	
	public GCompEventsGlobalData get() {
		return _synData;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this._synData.add(eventsType, eventsData);
	}
}
