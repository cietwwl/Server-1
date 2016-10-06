package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompEventsGlobalData;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GCompEventsDataDAO {

	private static final GCompEventsDataDAO _instance = new GCompEventsDataDAO();

	public static final GCompEventsDataDAO getInstance() {
		return _instance;
	}
	
	private GCompEventsGlobalData _currentGlobalData;
	
	public void loadEventsGlobalData() {
		String attr = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_CURRENT);
		if (attr != null && (attr = attr.trim()).length() > 0) {
			_currentGlobalData = JsonUtil.readValue(attr, GCompEventsGlobalData.class);
		} else {
			_currentGlobalData = new GCompEventsGlobalData();
		}
	}
	
	public GCompEventsGlobalData getCurrentGlobalData() {
		return _currentGlobalData;
	}
	
	public void update() {
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_CURRENT, JsonUtil.writeValue(_currentGlobalData));
	}
}
