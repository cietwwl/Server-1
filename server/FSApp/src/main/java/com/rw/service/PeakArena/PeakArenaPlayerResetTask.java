package com.rw.service.PeakArena;

import com.playerdata.Player;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rwbase.common.timer.IPlayerOperable;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class PeakArenaPlayerResetTask implements IPlayerOperable {

	private CfgOpenLevelLimitDAO _cfgOpenLevelLimitDAO;

	public PeakArenaPlayerResetTask() {
		_cfgOpenLevelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
	}

	@Override
	public boolean isInterestingOn(Player player) {
		return _cfgOpenLevelLimitDAO.isOpen(eOpenLevelType.PEAK_ARENA, player);
	}

	@Override
	public void operate(Player player) {
		TablePeakArenaData data = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (data != null) {
			data.setScore(0);
			data.resetRewardList();
			// 通知更新UserGameData
			player.getUserGameDataMgr().updatePeakArenaScore(0);
		}
	}

}
