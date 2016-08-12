package com.rwbase.dao.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

public class WakenLotteryDrawCfgDAO extends CfgCsvDao<WakenLotteryDrawCfg>{

	public static WakenLotteryDrawCfgDAO getInstance(){
		return SpringContextUtil.getBean(WakenLotteryDrawCfgDAO.class);
	}
	
	@Override
	protected Map<String, WakenLotteryDrawCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("store/WakenLotteryDrawCfg.csv", WakenLotteryDrawCfg.class);
		parse();
		return cfgCacheMap;
	}

	public WakenLotteryDrawCfg getCfgByType(int lotteryDrawType, int level){
		for (Iterator<Entry<String, WakenLotteryDrawCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, WakenLotteryDrawCfg> next = iterator.next();
			WakenLotteryDrawCfg cfg = next.getValue();
			if(cfg.getLotteryDrawType() == lotteryDrawType && cfg.getMinLevel() <= level && cfg.getMaxLevel() >= level){
				return cfg;
			}	
		}
		return null;
	}
	
	private void parse(){
		for (Iterator<Entry<String, WakenLotteryDrawCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, WakenLotteryDrawCfg> entry = iterator.next();
			WakenLotteryDrawCfg cfg = entry.getValue();
			String lvPeriod = cfg.getLvPeriod();
			String[] split = lvPeriod.split(":");
			int minLv = Integer.parseInt(split[0]);
			int maxLv = Integer.parseInt(split[1]);
			cfg.setMinLevel(minLv);
			cfg.setMaxLevel(maxLv);
			
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			String[] split1 = cfg.getFirstConsume().split(":");
			map.put(Integer.parseInt(split1[0]), Integer.parseInt(split1[1]));
			String[] split2 = cfg.getSecondConsume().split(":");
			map.put(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));  
			cfg.setConsumeMap(map);
			
			
			ArrayList<Integer> firstFreeRewardPlanList = parseScript(cfg.getFirstFreeRewardPlan());
			cfg.setFirstFreeRewardPlanList(firstFreeRewardPlanList);
			
			ArrayList<Integer> firstPayRewardPlanList = parseScript(cfg.getFirstPayRewardPlan());
			cfg.setFirstPayRewardPlanList(firstPayRewardPlanList);
			
			ArrayList<Integer> payRewardPlanList = parseScript(cfg.getPayRewardPlan());
			cfg.setPayRewardPlanList(payRewardPlanList);
			
			ArrayList<Integer> freeRewardPlanList = parseScript(cfg.getFreeRewardPlan());
			cfg.setFreeRewardPlanList(freeRewardPlanList);
			
			ArrayList<Integer> guaranteeRewardPlanList = parseScript(cfg.getGuaranteeRewardPlan());
			cfg.setGuaranteeRewardPlanList(guaranteeRewardPlanList);
		}
	}
	
	private ArrayList<Integer> parseScript(String value){
		String[] split = value.split(",");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (String str : split) {
			list.add(Integer.parseInt(str));
		}
		return list;
	}
	
}
