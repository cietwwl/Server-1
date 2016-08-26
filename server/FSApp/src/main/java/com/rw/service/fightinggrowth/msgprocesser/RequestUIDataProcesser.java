package com.rw.service.fightinggrowth.msgprocesser;

import com.google.protobuf.ByteString;
import com.playerdata.FSuserFightingGrowthMgr;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.fightgrowth.FSUserFightingGrowthSynData;
import com.rw.service.fightinggrowth.IMsgProcesser;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.RequestProtos.Request;

public class RequestUIDataProcesser implements IMsgProcesser {

	@Override
	public ByteString process(Player player, Request request) {
		FSUserFightingGrowthSynData synData = FSuserFightingGrowthMgr.getInstance().getFightingGrowthSynData(player);
		ClientDataSynMgr.synData(player, synData, eSynType.FIGHTING_GROWTH_DATA, eSynOpType.UPDATE_SINGLE);
		return ByteString.EMPTY;
	}

}
