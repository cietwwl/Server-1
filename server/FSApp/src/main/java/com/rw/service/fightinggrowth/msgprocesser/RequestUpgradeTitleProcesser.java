package com.rw.service.fightinggrowth.msgprocesser;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.FSuserFightingGrowthMgr;
import com.rw.fsutil.common.Pair;
import com.rw.service.fightinggrowth.IMsgProcesser;
import com.rwproto.FightGrowthProto.UpgradeFightingTitleResponse;
import com.rwproto.RequestProtos.Request;

public class RequestUpgradeTitleProcesser implements IMsgProcesser {

	@Override
	public ByteString process(Player player, Request request) {
		Pair<String, Boolean> upgradeResult = FSuserFightingGrowthMgr.getInstance().upgradeFightingTitle(player);
		UpgradeFightingTitleResponse.Builder resp = UpgradeFightingTitleResponse.newBuilder();
		resp.setTips(upgradeResult.getT1());
		resp.setSuccess(upgradeResult.getT2());
		return resp.build().toByteString();
	}

}
