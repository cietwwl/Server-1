package com.rwbase.dao.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.store.pojo.WakenLotteryRewardPoolCfg;

public class WakenLotteryRewardPoolCfgDAO extends CfgCsvDao<WakenLotteryRewardPoolCfg>{
	
	private HashMap<Integer, List<WakenLotteryRewardPoolCfg>> WakenLotteryRewardPoolMap = new HashMap<Integer, List<WakenLotteryRewardPoolCfg>>();
	/**
	 * 奖励池的权重和
	 */
	private HashMap<Integer, Integer> SumWeightMap = new HashMap<Integer, Integer>();
	
	public static WakenLotteryRewardPoolCfgDAO getInstance() {
		return SpringContextUtil.getBean(WakenLotteryRewardPoolCfgDAO.class);
	}

	@Override
	protected Map<String, WakenLotteryRewardPoolCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("store/WakenLotteryRewardPoolCfg.csv",WakenLotteryRewardPoolCfg.class);
		parse();
		return cfgCacheMap;
	}
	
	
	private void parse(){
		
		for (Iterator<Entry<String, WakenLotteryRewardPoolCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, WakenLotteryRewardPoolCfg> next = iterator.next();
			WakenLotteryRewardPoolCfg cfg = next.getValue();
			int poolId = cfg.getPoolId();
			if (WakenLotteryRewardPoolMap.containsKey(poolId)) {
				List<WakenLotteryRewardPoolCfg> list = WakenLotteryRewardPoolMap.get(poolId);
				list.add(cfg);
			} else {
				List<WakenLotteryRewardPoolCfg> list = new ArrayList<WakenLotteryRewardPoolCfg>();
				list.add(cfg);
				WakenLotteryRewardPoolMap.put(poolId, list);
			}
			Integer value = SumWeightMap.get(poolId);
			int sum = cfg.getWeight();
			if (value != null) {
				sum += value;
			}
			SumWeightMap.put(poolId, sum);
		}
	}
	
	public List<WakenLotteryRewardPoolCfg> getWakenLotteryRewardPoolCfgByPoolId(int poolId){
		List<WakenLotteryRewardPoolCfg> list = WakenLotteryRewardPoolMap.get(poolId);
		return list;
	}
	
	public int getSumWeightByPoolId(int poolId){
		return SumWeightMap.get(poolId);
	}
}
