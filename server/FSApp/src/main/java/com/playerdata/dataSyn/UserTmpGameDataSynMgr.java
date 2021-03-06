package com.playerdata.dataSyn;

import com.playerdata.Player;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserTmpGameDataSynMgr {

	private static UserTmpGameDataSynMgr instance = new UserTmpGameDataSynMgr();

	public static UserTmpGameDataSynMgr getInstance() {
		return instance;
	}

	public void synDataByFlag(Player player) {
		UserTmpGameDataFlag userTmpGameDataFlag = player.getUserTmpGameDataFlag();
		boolean synAll = userTmpGameDataFlag.isSynAll();

		UserTmpGameData userTmpGameData = new UserTmpGameData();
		boolean doSyn = false;
		if (synAll || userTmpGameDataFlag.isSynFightingAll()) {
			// int fightingAll = player.getHeroMgr().getFightingAll();
//			int fightingAll = player.getHeroMgr().getFightingAll(player);
			
			//要求改回五人小组的战力
			int fightingAll = player.getHeroMgr().getFightingTeam(player);

			userTmpGameData.setFightingAll(fightingAll);
			doSyn = true;

			userTmpGameDataFlag.setSynFightingAll(false);

		}

		if (doSyn) {
			ClientDataSynMgr.synData(player, userTmpGameData, eSynType.USER_TMP_GAME_DATA, eSynOpType.UPDATE_SINGLE);
		}
		userTmpGameDataFlag.setSynAll(false);
	}

}
