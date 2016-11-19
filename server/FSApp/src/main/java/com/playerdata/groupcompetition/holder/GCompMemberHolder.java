package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompMemberHolder {

	private static GCompMemberHolder _instance = new GCompMemberHolder();
	
	private static final eSynType synType = eSynType.GCompMember;
	private static final eSynOpType synOpType = eSynOpType.UPDATE_SINGLE;
	
	public static GCompMemberHolder getInstance() {
		return _instance;
	}
	
	GCompMemberHolder() {
	}
	
	public void syn(Player player, GCompMember member) {
		ClientDataSynMgr.synData(player, member, synType, synOpType);
	}
}
