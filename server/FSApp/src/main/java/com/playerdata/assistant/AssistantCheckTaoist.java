package com.playerdata.assistant;

import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.UserGameDataMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistConsumeCfgHelper;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantCheckTaoist extends DefaultAssistantChecker {

	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		TaoistMagicCfgHelper helper = TaoistMagicCfgHelper.getInstance();
		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		TaoistConsumeCfgHelper consumeHelper = TaoistConsumeCfgHelper.getInstance();

		int plvl = player.getLevel();
		int[] planNums = new int[1];
		planNums[0]=1;
		
		Iterable<Entry<Integer, Integer>> lst = player.getTaoistMgr().getAllTaoist();
		for (Entry<Integer, Integer> entry : lst) {
			int taoistID = entry.getKey();
			int level = entry.getValue();
			TaoistMagicCfg cfg = helper.getCfgById(String.valueOf(taoistID));
			int consumeId = cfg.getConsumeId();
			int maxCfgLevel = consumeHelper.getMaxLevel(consumeId);
			int newLevel = level +1;
			if (newLevel > maxCfgLevel) {
				continue;
			}
			if (newLevel > plvl) {
				continue;
			}
			int coinCount = consumeHelper.getConsumeCoin(consumeId, level, planNums);
			if (coinCount == -1) {
				continue;
			}
			if (userGameDataMgr.isEnoughCurrency(cfg.getCoinType(), coinCount)) {
				param = String.valueOf(taoistID);
				return AssistantEventID.GotoTaoist;
			}
		}
		return null;
	}

}
