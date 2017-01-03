package com.rwbase.common.herosynhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.common.IHeroSynHandler;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.rwproto.BattleCommon.eBattlePositionType;

public class CommonHeroSynHandler extends BattleHeroSynBase implements IHeroSynHandler {

	@Override
	public void synHeroData(Player player, eBattlePositionType posKey, String key) {
		try {
			List<EmbattlePositionInfo> allPosInfo = EmbattleInfoMgr.getMgr().getAllEmbattlePositionInfo(player.getUserId(), posKey.getNumber());
			List<EmbattleHeroPosition> posList;
			if (allPosInfo == null || allPosInfo.isEmpty()) {
				posList = Collections.emptyList();
			} else {
				posList = new ArrayList<EmbattleHeroPosition>(allPosInfo.size() * 5);
				for (int i = 0; i < allPosInfo.size(); i++) {
					posList.addAll(allPosInfo.get(i).getPos());
				}
			}
//			System.err.println("==========>>>>>>>>>> 准备下发英雄数据，类型：" + posKey + "，PosList.size()=" + posList.size() + " <<<<<<<<<<==========");
			synHeros(player, posList);
		} catch (Exception e) {
			GameLog.error("CommonHeroSynHandler", player.getUserId(), "下发英雄数据出现异常！posKey=" + posKey, e);
		}
	}

}
