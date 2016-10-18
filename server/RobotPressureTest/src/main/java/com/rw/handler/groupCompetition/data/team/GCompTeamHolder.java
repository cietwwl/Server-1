package com.rw.handler.groupCompetition.data.team;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompTeamHolder {
	
	private SynDataListHolder<GCompTeam> _dataHolder = new SynDataListHolder<GCompTeam>(GCompTeam.class);
	private GCompTeam team;
	private long personalMatchingTimeOut;
	private long teamWaitingTimeout;
	private long lastSendReadyTime;
	
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
	
	public void clearTeam() {
		team = null;
	}

	public long getPersonalMatchingTimeOut() {
		return personalMatchingTimeOut;
	}

	public void setPersonalMatchingTimeOut(long personalMatchingTimeOut) {
		this.personalMatchingTimeOut = personalMatchingTimeOut;
	}

	public long getTeamWaitingTimeout() {
		return teamWaitingTimeout;
	}

	public void setTeamWaitingTimeout(long teamWaitingTimeout) {
		this.teamWaitingTimeout = teamWaitingTimeout;
	}

	public long getLastSendReadyTime() {
		return lastSendReadyTime;
	}

	public void setLastSendReadyTime(long lastSendReadyTime) {
		this.lastSendReadyTime = lastSendReadyTime;
	}
}
