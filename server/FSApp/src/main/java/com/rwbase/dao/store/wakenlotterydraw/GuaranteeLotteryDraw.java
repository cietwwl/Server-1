package com.rwbase.dao.store.wakenlotterydraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.Player;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.store.pojo.StoreData;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.TableStore;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

/**
 * 保底
 * @author lida
 *
 */
public class GuaranteeLotteryDraw implements IWakenLotteryDraw{

	@Override
	public HashMap<Integer, Integer> lotteryDraw(Player player, StoreDataHolder holder, WakenLotteryDrawCfg cfg) {
		// TODO Auto-generated method stub
		ArrayList<Integer> planList = cfg.getGuaranteeRewardPlanList();
		WakenLotteryResult result = WakenLotteryProcesser.getInstantce().processLottery(planList);
		TableStore tableStore = holder.get();
		ConcurrentHashMap<Integer,StoreData> storeDataMap = tableStore.getStoreDataMap();
		StoreData storeData = storeDataMap.get(eStoreType.Waken.getOrder());
		storeData.setRecordGuaranteeTime(0);
		
		
		return result.getRewardMap();
	}

}