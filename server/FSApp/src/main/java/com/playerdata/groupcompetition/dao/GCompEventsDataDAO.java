package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.dao.gameworld.GameWorldAttributeData;
import com.rwbase.dao.gameworld.GameWorldDAO;
import com.rwbase.gameworld.GameWorldKey;

public class GCompEventsDataDAO {

	private static final GCompEventsDataDAO _instance = new GCompEventsDataDAO();

	public static final GCompEventsDataDAO getInstance() {
		return _instance;
	}
	
	private final GCompEventsGlobalData _currentGlobalData = new GCompEventsGlobalData();
	private final GCompEventsGlobalData _lastGlobalData = new GCompEventsGlobalData();
	
	private void save(GameWorldKey key, GCompEventsGlobalData data) {
		GameWorldAttributeData gwad = new GameWorldAttributeData();
		gwad.setKey(key.getName());
		gwad.setValue(JsonUtil.writeValue(data));
		GameWorldDAO.getInstance().update(gwad);
	}
	
	public GCompEventsGlobalData getCurrentGlobalData() {
		return _currentGlobalData;
	}
	
	public GCompEventsGlobalData getLastGlobalData() {
		return _lastGlobalData;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this._currentGlobalData.add(eventsType, eventsData);
		this.save(GameWorldKey.GROUP_COMPETITION_AGAINSTS_CURRENT, _currentGlobalData);
	}
	
	public void saveCurrentToLast() {
		_lastGlobalData.copy(_currentGlobalData);
		this.save(GameWorldKey.GROUP_COMPETITION_AGAINSTS_LAST, _lastGlobalData);
	}
}
