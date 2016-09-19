package com.playerdata.groupcompetition.holder;

import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;

public class GCompFightingRecordMgr {

	private static final GCompFightingRecordMgr _instance = new GCompFightingRecordMgr();
	
	public static GCompFightingRecordMgr getInstance() {
		return _instance;
	}
	
	private final GCompFightingRecordHolder _dataHolder;
	
	protected GCompFightingRecordMgr() {
		_dataHolder = GCompFightingRecordHolder.getInstance();
	}
	
	public void initRecordList(int matchId) {
		_dataHolder.initRecordList(matchId);
	}
	
	public void addFightingRecord(int matchId, GCompFightingRecord record) {
		_dataHolder.add(matchId, record);
	}
}
