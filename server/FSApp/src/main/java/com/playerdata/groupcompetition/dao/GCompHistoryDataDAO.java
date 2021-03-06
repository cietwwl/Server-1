package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompHistoryData;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GCompHistoryDataDAO {

	private static GCompHistoryDataDAO _instance = new GCompHistoryDataDAO();
	
	public static GCompHistoryDataDAO getInstance() {
		return _instance;
	}
	
	private GCompHistoryData _data;
	
	public void loadHistoryData() {
		String data = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_LAST);
		if(data != null && data.length() > 0) {
			_data = JsonUtil.readValue(data, GCompHistoryData.class);
		}  else {
			_data = GCompHistoryData.createNew();
		}
	}

	public GCompHistoryData get() {
		return _data;
	}
	
	public void update() {
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_LAST, JsonUtil.writeValue(_data));
	}
}
