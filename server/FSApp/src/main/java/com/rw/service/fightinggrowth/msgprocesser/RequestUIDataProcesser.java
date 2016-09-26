package com.rw.service.fightinggrowth.msgprocesser;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.FSuserFightingGrowthMgr;
import com.rw.service.fightinggrowth.IMsgProcesser;
import com.rwproto.RequestProtos.Request;

public class RequestUIDataProcesser implements IMsgProcesser {

	@Override
	public ByteString process(Player player, Request request) {
//		FSuserFightingGrowthMgr.getInstance().getHolder().synData(player);
		return FSuserFightingGrowthMgr.getInstance().getHolder().createFightingGrowthSynData(player).toByteString();
	}

}
