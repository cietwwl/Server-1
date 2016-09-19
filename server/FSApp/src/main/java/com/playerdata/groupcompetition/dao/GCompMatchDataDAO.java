package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompMatchSynData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompMatchDataDAO {

	private static final GCompMatchDataDAO _instance = new GCompMatchDataDAO();

	public static final GCompMatchDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompMatchSynData _synData = new GCompMatchSynData();
	
	public GCompMatchSynData get() {
		return _synData;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this._synData.add(eventsType, eventsData);
	}
}
