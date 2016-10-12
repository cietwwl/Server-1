package com.rwbase.dao.store.wakenlotterydraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.Player;
import com.playerdata.StoreMgr;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.store.pojo.StoreData;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

public class FirstFreeWakenLotteryDraw implements IWakenLotteryDraw{

	@Override
	public HashMap<Integer, Integer> lotteryDraw(Player player, StoreDataHolder holder, WakenLotteryDrawCfg cfg) {
		// TODO Auto-generated method stub
		ArrayList<Integer> firstFreeRewardPlanList = cfg.getFirstFreeRewardPlanList();
		
		WakenLotteryResult result = WakenLotteryProcesser.getInstantce().processLottery(firstFreeRewardPlanList);
		TableStore tableStore = holder.get();
		ConcurrentHashMap<Integer,StoreData> storeDataMap = tableStore.getStoreDataMap();
		StoreData storeData = storeDataMap.get(eStoreType.Waken.getOrder());
		storeData.setFirstFreeLottery(false);
		storeData.setDrawTime(storeData.getDrawTime() + 1);
		
		return result.getRewardMap();
	}

}
