package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GCompEventsDataDAO {

	private static final GCompEventsDataDAO _instance = new GCompEventsDataDAO();

	public static final GCompEventsDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompEventsGlobalData _currentGlobalData = new GCompEventsGlobalData();
	
	public GCompEventsGlobalData getCurrentGlobalData() {
		return _currentGlobalData;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this._currentGlobalData.add(eventsType, eventsData);
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_CURRENT, JsonUtil.writeValue(_currentGlobalData));
	}
	
	public void update() {
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_CURRENT, JsonUtil.writeValue(_currentGlobalData));
	}
}
