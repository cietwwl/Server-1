package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;
import com.rwproto.GroupCompetitionProto.CommonGetDataRspMsg;
import com.rwproto.GroupCompetitionProto.CommonGetDataRspMsg.Builder;
import com.rwproto.GroupCompetitionProto.GCResultType;

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
	
	public void endLiveRecord() {
		_dataHolder.endAllLiveMatch();
	}
	
	public void addFightingRecord(int matchId, GCompFightingRecord record) {
		_dataHolder.add(matchId, record);
	}
	
	public void getFightRecordLive(Player player, CommonGetDataRspMsg.Builder builder, int matchId, long time){
		_dataHolder.getFightRecordLive(player, matchId, time);
		builder.setRstType(GCResultType.SUCCESS);
	}
	
	public void getFightRecord(Player player, CommonGetDataRspMsg.Builder builder, int matchId, long time){
		_dataHolder.syn(player, matchId, time);
		builder.setRstType(GCResultType.SUCCESS);
	}

	public void leaveLivePage(Player player, Builder builder, int matchId) {
		_dataHolder.leaveLivePage(player, matchId);
		builder.setRstType(GCResultType.SUCCESS);
	}
	
	public void deleteLastFightRecord(List<Integer> matchList){
		_dataHolder.deleteLastSessionRecord(matchList);
	}
}
