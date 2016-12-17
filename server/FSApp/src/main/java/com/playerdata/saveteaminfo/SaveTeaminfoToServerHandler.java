package com.playerdata.saveteaminfo;

import java.util.List;

import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwproto.BattleCommon.BattleCommonReqMsg;
import com.rwproto.BattleCommon.BattleCommonRspMsg;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.BattleCommon.eBattlePositionType;

public class SaveTeaminfoToServerHandler {

	private static SaveTeaminfoToServerHandler instance = new SaveTeaminfoToServerHandler();

	protected SaveTeaminfoToServerHandler() {
	}

	public static SaveTeaminfoToServerHandler getInstance() {
		return instance;
	}

	public ByteString putTeamInfoToServer(Player player, BattleCommonReqMsg msgMSRequest) {
		BattleCommonRspMsg.Builder msRsp = BattleCommonRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		msRsp.setIsSuccess(true);
		eBattlePositionType type = msgMSRequest.getPositionType();
		String str = msgMSRequest.getRecordkey();
		List<BattleHeroPosition> positionList = msgMSRequest.getBattleHeroPositionList();

		List<EmbattleHeroPosition> parseList = EmbattlePositonHelper.parseMsgHeroPos2Memery(positionList);

		if (parseList.isEmpty() && type == eBattlePositionType.Normal) {
			// 2016-10-18 by Perry：empty肯定是一个错误的数据，为了保持这个数据的正确性，只能把主角放进去
			// 2016-10-24 by Perry：改为只判断normal，有些阵容反而是不保存主角的
			EmbattleHeroPosition pos = new EmbattleHeroPosition();
			pos.setId(player.getUserId());
			pos.setPos(0);
			parseList.add(pos);
		}

		// 存储到阵容中
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, type.getNumber(), str, parseList);

		// 通知阵容发生了改变
		FSHeroMgr.getInstance().updateFightingTeamWhenEmBattleChange(player);

		// 通知到万仙阵这里修改了副本的阵容
		AngelArrayTeamInfoHelper.updateRankingEntryWhenNormalEmbattleChange(player);

		return msRsp.build().toByteString();
	}
}