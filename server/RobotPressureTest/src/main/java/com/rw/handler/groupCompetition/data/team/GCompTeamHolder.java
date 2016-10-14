package com.rw.handler.groupCompetition.data.team;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompTeamHolder {
	
	private SynDataListHolder<GCompTeam> _dataHolder = new SynDataListHolder<GCompTeam>(GCompTeam.class);
	private GCompTeam team;
	private long personalMatchingTimeOut;
	
	public void syn(MsgDataSyn msgDataSyn) {
		_dataHolder.Syn(msgDataSyn);
		List<GCompTeam> list = _dataHolder.getItemList();
		if(list.size() > 0) {
			team = list.get(0);
		} else {
			team = null;
		}
	}
	
	public GCompTeam getTeam() {
		return team;
	}

	public long getPersonalMatchingTimeOut() {
		return personalMatchingTimeOut;
	}

	public void setPersonalMatchingTimeOut(long personalMatchingTimeOut) {
		this.personalMatchingTimeOut = personalMatchingTimeOut;
	}
}
