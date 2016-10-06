package com.playerdata.groupcompetition.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 
 * 以matchId为主键，记录帮派争霸一场比赛的详细信息
 * 
 * @author CHEN.P
 *
 */
public class GCompDetailInfoDAO {

	private static final GCompDetailInfoDAO _instance = new GCompDetailInfoDAO();
	
	public static final GCompDetailInfoDAO getInstance() {
		return _instance;
	}
	
	private Map<Integer, GCompDetailInfo> _dataMap;
	
	public void loadDetailInfo() {
		String value = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_DETAIL);
		_dataMap = new HashMap<Integer, GCompDetailInfo>();
		if (value != null && value.length() > 0) {
			Map<String, Object> map = JsonUtil.readJson2Map(value, GCompDetailInfo.class);
			for(Iterator<String> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
				String key = keyItr.next();
				GCompDetailInfo detailInfo = (GCompDetailInfo)map.get(key);
				_dataMap.put(Integer.parseInt(key), detailInfo);
			}
		}
	}
	
	public void reset() {
		_dataMap.clear();
	}

	public GCompDetailInfo getDetailInfo(int matchId) {
		return _dataMap.get(matchId);
	}
	
	public void addDetailInfo(GCompDetailInfo info) {
		_dataMap.put(info.getMatchId(), info);
	}
	
	public void update() {
		String value = JsonUtil.writeValue(_dataMap);
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_AGAINSTS_DETAIL, value);
	}
}
