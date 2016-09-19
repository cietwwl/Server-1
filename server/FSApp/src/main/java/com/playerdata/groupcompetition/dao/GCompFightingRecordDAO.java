package com.playerdata.groupcompetition.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;

public class GCompFightingRecordDAO {

	private static final GCompFightingRecordDAO _instance = new GCompFightingRecordDAO();
	
	public static final GCompFightingRecordDAO getInstance() {
		return _instance;
	}
	
	private final Map<Integer, List<GCompFightingRecord>> _dataMap = new ConcurrentHashMap<Integer, List<GCompFightingRecord>>();
	
	public List<GCompFightingRecord> getFightingRecord(int matchId) {
		return _dataMap.get(matchId);
	}
	
	public void initRecordList(int matchId) {
		_dataMap.put(matchId, new ArrayList<GCompFightingRecord>());
	}
	
	public void add(int matchId, GCompFightingRecord record) {
		List<GCompFightingRecord> list = _dataMap.get(matchId);
		synchronized(list) {
			list.add(record);
		}
	}
}
