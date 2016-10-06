package com.playerdata.groupcompetition.dao;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;

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
	
	private final Map<Integer, GCompDetailInfo> _dataMap = new HashMap<Integer, GCompDetailInfo>();

	public GCompDetailInfo getDetailInfo(int matchId) {
		return _dataMap.get(matchId);
	}
	
	public void addDetailInfo(GCompDetailInfo info) {
		_dataMap.put(info.getMatchId(), info);
	}
}
