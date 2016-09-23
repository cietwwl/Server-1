package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompEventsSynData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompEventsDataDAO {

	private static final GCompEventsDataDAO _instance = new GCompEventsDataDAO();

	public static final GCompEventsDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompEventsSynData _synData = new GCompEventsSynData();
	
	public GCompEventsSynData get() {
		return _synData;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this._synData.add(eventsType, eventsData);
	}
}
