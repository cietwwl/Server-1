package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
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
	
	public void initRecordList(List<GCompAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			_dataHolder.initRecordList(againsts.get(i).getId());
		}
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
}
