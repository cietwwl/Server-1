package com.playerdata.groupcompetition.dao;

import java.util.LinkedList;

import com.playerdata.groupcompetition.holder.data.GCompFightRecordData;
import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;
import com.rw.fsutil.cacheDao.DataKVDao;

public class GCompFightingRecordDAO extends DataKVDao<GCompFightRecordData>{

	private static GCompFightingRecordDAO _instance = new GCompFightingRecordDAO();
	public static int MAX_RECORD_COUNT = 50;
	
	public static GCompFightingRecordDAO getInstance() {
		return _instance;
	}
		
	public LinkedList<GCompFightingRecord> getFightingRecord(int matchId) {
		GCompFightRecordData matchRecord = get(String.valueOf(matchId));
		if(null == matchRecord){
			return null;
		}
		return matchRecord.getRecord();
	}
	
	public void initRecordList(int matchId) {
		if(null == get(String.valueOf(matchId))){
			GCompFightRecordData matchRecord = new GCompFightRecordData();
			matchRecord.setMatchId(matchId);
			matchRecord.setRecord(new LinkedList<GCompFightingRecord>());
			update(matchRecord);
		}
	}
	
	public void add(int matchId, GCompFightingRecord record) {
		LinkedList<GCompFightingRecord> list = getFightingRecord(matchId);
		if(null == list){
			return;
		}
		synchronized(list) {
			int listSize = list.size();
			while(listSize >= MAX_RECORD_COUNT){
				list.remove();
				listSize--;
			}
			list.add(record);
		}
		update(String.valueOf(matchId));
	}
}
