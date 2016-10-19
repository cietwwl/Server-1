package com.rw.handler.groupCompetition.data.events;

import java.util.List;

import com.rw.dataSyn.DataSynHelper;
import com.rw.handler.groupCompetition.stageimpl.GCompAgainst;
import com.rw.handler.groupCompetition.util.GCEventsType;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompEventsDataHolder {

	private GCompEventsData eventsData;
	private long waitingTimeout;

	public void syn(MsgDataSyn msgDataSyn) {
		switch (msgDataSyn.getSynOpType()) {
		case UPDATE_LIST:
		case UPDATE_SINGLE:
			eventsData = DataSynHelper.ToObject(GCompEventsData.class, msgDataSyn.getSynData(0).getJsonData());
			break;
		case REMOVE_SINGLE:
			eventsData = null;
			break;
		default:
			break;
		}
	}
	
	public GCEventsType getCurrentEventsType() {
		List<GCompAgainst> list = eventsData.getMatches();
		if(list.size() > 0) {
			return list.get(list.size()).getTopType();
		} else {
			return GCEventsType.TOP_8;
		}
	}
	
	public boolean isNull() {
		return eventsData == null;
	}
	
	public void clear() {
		this.eventsData = null;
	}

	public long getWaitingTimeout() {
		return waitingTimeout;
	}

	public void setWaitingTimeout(long waitingTimeout) {
		this.waitingTimeout = waitingTimeout;
	}
}
