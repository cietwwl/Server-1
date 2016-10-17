package com.playerdata.saveteaminfo;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwproto.BattleCommon.BattleCommonReqMsg;
import com.rwproto.BattleCommon.BattleCommonRspMsg;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.BattleCommon.eBattlePositionType;

public class SaveTeaminfoToServerHandler {

	private static SaveTeaminfoToServerHandler instance;

	private SaveTeaminfoToServerHandler() {
	}

	public static SaveTeaminfoToServerHandler getInstance() {
		if (instance == null) {
			instance = new SaveTeaminfoToServerHandler();
		}
		return instance;
	}

	public ByteString putTeamInfoToServer(Player player, BattleCommonReqMsg msgMSRequest) {
		BattleCommonRspMsg.Builder msRsp = BattleCommonRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		msRsp.setIsSuccess(true);
		eBattlePositionType type = msgMSRequest.getPositionType();
		String str = msgMSRequest.getRecordkey();
		List<BattleHeroPosition> positionList = msgMSRequest.getBattleHeroPositionList();

		// 存储到阵容中
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, type.getNumber(), str, EmbattlePositonHelper.parseMsgHeroPos2Memery(positionList));

		// 通知阵容发生了改变
		FSHeroMgr.getInstance().updateFightingTeamWhenEmBattleChange(player.getUserId());

		return msRsp.build().toByteString();
	}
}