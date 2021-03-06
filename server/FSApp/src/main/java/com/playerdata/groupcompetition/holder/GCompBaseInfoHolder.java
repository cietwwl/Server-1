package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.holder.data.GCompBaseInfo;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompBaseInfoHolder {
	
	private static GCompBaseInfoHolder _instance = new GCompBaseInfoHolder();
	
	public static GCompBaseInfoHolder getInstance() {
		return _instance;
	}
	
	private eSynType _synType = eSynType.GCompBase;
	protected GCompBaseInfoHolder() {
	}

	public void syn(Player player, GCompBaseInfo baseInfo) {
		ClientDataSynMgr.synData(player, baseInfo, _synType, eSynOpType.UPDATE_SINGLE);
		GCompUtil.log("----------同步数据给：{}，baseInfo：{}----------", player, baseInfo);
	}

	public void synToAll(GCompBaseInfo baseInfo) {
		GCompUtil.log("----------同步数据给所有玩家，baseInfo：{}----------", baseInfo);
		SynToAllTask.createNewTaskAndSubmit(baseInfo, _synType, eSynOpType.UPDATE_SINGLE);
	}
}
