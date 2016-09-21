package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.data.GCompBaseInfo;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompBaseInfoHolder {
	
	private static final GCompBaseInfoHolder _instance = new GCompBaseInfoHolder();
	
	public static final GCompBaseInfoHolder getInstance() {
		return _instance;
	}
	
	private eSynType _synType = eSynType.GCompBase;
	protected GCompBaseInfoHolder() {
	}
	
	private GCompBaseInfo createBaseInfo() {
		return GroupCompetitionMgr.getInstance().createBaseInfoSynData();
	}
	
	public void syn(Player player) {
		GCompBaseInfo baseInfo = createBaseInfo();
		ClientDataSynMgr.synData(player, baseInfo, _synType, eSynOpType.UPDATE_SINGLE);
		System.err.println("----------同步数据，baseInfo：" + baseInfo + "----------");
	}
	
	public void synToAll() {
		SynToAllTask.createNewTaskAndSubmit(createBaseInfo(), _synType, eSynOpType.UPDATE_SINGLE);
	}
}
