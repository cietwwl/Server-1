package com.rwbase.dao.store.wakenlotterydraw;

import java.util.HashMap;

import com.playerdata.Player;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

public interface IWakenLotteryDraw {
	public HashMap<Integer, Integer> lotteryDraw(Player player, StoreDataHolder holder, WakenLotteryDrawCfg cfg);
}
