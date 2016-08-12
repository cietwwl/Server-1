package com.rwbase.dao.store.wakenlotterydraw;

import java.util.ArrayList;
import java.util.HashMap;

import com.playerdata.Player;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

/**
 * 付费抽箱
 * @author lida
 *
 */
public class PayWakenLotteryDraw implements IWakenLotteryDraw{

	@Override
	public HashMap<Integer, Integer> lotteryDraw(Player player, StoreDataHolder holder, WakenLotteryDrawCfg cfg) {
		// TODO Auto-generated method stub
		ArrayList<Integer> planList = cfg.getPayRewardPlanList();

		WakenLotteryResult result = WakenLotteryProcesser.getInstantce().processLottery(planList);
		
		
		return result.getRewardMap();
	}

}
