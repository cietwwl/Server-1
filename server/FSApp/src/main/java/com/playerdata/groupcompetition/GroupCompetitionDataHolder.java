package com.playerdata.groupcompetition;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GroupCompetitionDataHolder {

	private static final GroupCompetitionDataHolder _instance = new GroupCompetitionDataHolder();
	
	private GroupCompetitionGlobalData _data;
	
	public static GroupCompetitionDataHolder getInstance() {
		return _instance;
	}
	
	void loadGroupCompetitionGlobalData() {
		if (_data == null) {
			synchronized (this) {
				if (_data == null) {
					String attrData = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION);
					if (attrData != null) {
						_data = JsonUtil.readValue(attrData, GroupCompetitionGlobalData.class);
					} else {
						_data = GroupCompetitionGlobalData.createEmpty();
					}
				}
			}
		}
	}
	
	GroupCompetitionGlobalData get() {
		return _data;
	}
	
	void update() {
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION, JsonUtil.writeValue(_data));
	}
}
