package com.rwbase.dao.store.wakenlotterydraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.experimental.results.ResultMatchers;

import com.playerdata.Player;
import com.rwbase.dao.store.pojo.StoreDataHolder;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

public class TenWakenLotteryDraw implements IWakenLotteryDraw{

	@Override
	public HashMap<Integer, Integer> lotteryDraw(Player player, StoreDataHolder holder, WakenLotteryDrawCfg cfg) {
		// TODO Auto-generated method stub
		int drawTime = cfg.getDrawTime();
		ArrayList<Integer> payRewardPlanList = cfg.getPayRewardPlanList();
		ArrayList<Integer> guaranteeRewardPlanList = cfg.getGuaranteeRewardPlanList();
		int guaranteeeTime = cfg.getGuaranteeeTime();
		HashMap<Integer, Integer> rewardMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < drawTime; i++) {
			WakenLotteryResult result;
			if(i+1 == guaranteeeTime){
				result = WakenLotteryProcesser.getInstantce().processLottery(guaranteeRewardPlanList);
			}else{
				result = WakenLotteryProcesser.getInstantce().processLottery(payRewardPlanList);
			}
			
			HashMap<Integer, Integer> map = result.getRewardMap();
			for (Iterator<Entry<Integer, Integer>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, Integer> entry = iterator.next();
				Integer key = entry.getKey();
				Integer count = entry.getValue();
				if (rewardMap.containsKey(key)) {
					Integer value = rewardMap.get(key);
					rewardMap.put(key, value + count);
				} else {
					rewardMap.put(key, count);
				}

			}
		}
		return rewardMap;
	}

}
