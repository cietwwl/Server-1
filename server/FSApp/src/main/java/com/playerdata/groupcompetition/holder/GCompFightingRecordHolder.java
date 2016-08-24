package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCompFightingRecordDAO;
import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompFightingRecordHolder {

	private static final GCompFightingRecordHolder _instance = new GCompFightingRecordHolder();
	
	public static final GCompFightingRecordHolder getInstance() {
		return _instance;
	}
	
	private GCompFightingRecordDAO _dao;
	
	protected GCompFightingRecordHolder() {
		
	}
	
	public void syn(Player player, int matchId) {
		List<GCompFightingRecord> allRecords = _dao.getFightingRecord(matchId);
		if (allRecords != null && allRecords.size() > 0) {
			ClientDataSynMgr.updateDataList(player, allRecords, eSynType.GCompFightingRecord, eSynOpType.UPDATE_LIST);
		}
	}
	
	public void initRecordList(int matchId) {
		_dao.initRecordList(matchId);
	}
	
	public void add(int matchId, GCompFightingRecord record) {
		_dao.add(matchId, record);
		// 同步到相关的人
	}
}
