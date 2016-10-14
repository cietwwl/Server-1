package com.rw.handler.groupCompetition.data.onlinemember;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompOnlineMemberHolder {
	
	private SynDataListHolder<GCompOnlineMember> _dataHolder = new SynDataListHolder<GCompOnlineMember>(GCompOnlineMember.class);
	private List<GCompOnlineMember> _allOnlineMembers = Collections.emptyList();
	private Random random = new Random();
	private Map<String, Long> inviteTimeoutMap = new HashMap<String, Long>();
	
	public void syn(MsgDataSyn msgDataSyn) {
		_dataHolder.Syn(msgDataSyn);
		_allOnlineMembers = _dataHolder.getItemList();
	}
	
	public int getSizeOfOnlineMember() {
		return _allOnlineMembers.size();
	}
	
	public GCompOnlineMember getRandomOnlineMember() {
		if(_allOnlineMembers.size() > 0) {
			return _allOnlineMembers.get(random.nextInt(_allOnlineMembers.size()));
		}
		return null;
	}
	
	public Long getInviteTimeout(String userId) {
		return inviteTimeoutMap.get(userId);
	}
	
	public void setInviteTimeout(String userId, long time) {
		inviteTimeoutMap.put(userId, time);
	}
}
