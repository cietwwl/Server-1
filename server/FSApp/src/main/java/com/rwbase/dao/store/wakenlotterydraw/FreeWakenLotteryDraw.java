package com.rwbase.dao.store.wakenlotterydraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.Player;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.store.WakenLotteryDrawCfgDAO;
import com.rwbase.dao.store.pojo.StoreData;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

/**
 * 免费觉醒抽相
 * @author lida
 *
 */
public class FreeWakenLotteryDraw implements IWakenLotteryDraw{

	@Override
	public HashMap<Integer, Integer> lotteryDraw(Player player, StoreDataHolder holder, WakenLotteryDrawCfg cfg) {
		// TODO Auto-generated method stub
		ArrayList<Integer> planList = cfg.getFreeRewardPlanList();
		WakenLotteryResult result = WakenLotteryProcesser.getInstantce().processLottery(planList);
		return result.getRewardMap();
	}

}
