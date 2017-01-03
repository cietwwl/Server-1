package com.rwbase.common.herosynhandler;

import java.util.ArrayList;
import java.util.List;

import com.common.IHeroSynHandler;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.rwproto.BattleCommon.eBattlePositionType;

public class PeakArenaHeroSynHandler extends BattleHeroSynBase implements IHeroSynHandler {

	private static List<String> peakArenaKeys = new ArrayList<String>();
	
	static {
		peakArenaKeys.add("0");
		peakArenaKeys.add("1");
		peakArenaKeys.add("2");
	}
	
	@Override
	public void synHeroData(Player player, eBattlePositionType posKey, String key) {
		try {
			int size = peakArenaKeys.size();
			List<EmbattleHeroPosition> posList = new ArrayList<EmbattleHeroPosition>(size * 5);
			for (int i = 0; i < size; i++) {
				EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.PeakArenaPos_VALUE, String.valueOf(i));
				if (positionInfo != null) {
					posList.addAll(positionInfo.getPos());
				}
			}
//			System.err.println("==========>>>>>>>>>> 准备下发英雄数据，类型：" + posKey + "，PosList.size()=" + posList.size() + " <<<<<<<<<<==========");
			synHeros(player, posList);
		} catch (Exception e) {
			GameLog.error("CommonHeroSynHandler", player.getUserId(), "下发英雄数据出现异常！posKey=" + posKey, e);
		}
	}

}
