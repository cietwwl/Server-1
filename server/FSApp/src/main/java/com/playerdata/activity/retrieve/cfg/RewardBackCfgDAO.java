package com.playerdata.activity.retrieve.cfg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RewardBackCfgDAO extends CfgCsvDao<RewardBackCfg>{
	
	public static RewardBackCfgDAO getInstance(){
		return SpringContextUtil.getBean(RewardBackCfgDAO.class);
	}
	

	@Override
	protected Map<String, RewardBackCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("rewardBack/RewardsBack.csv", RewardBackCfg.class);
		for (RewardBackCfg cfgTmp : cfgCacheMap.values()) {		
			parse(cfgTmp);
		}		
		return cfgCacheMap;
	}


	private void parse(RewardBackCfg cfgTmp) {
		String normalrewards = cfgTmp.getNormalRewards();		
		if(StringUtils.equals("-1", normalrewards)){
			
		}else{
			HashMap<Integer, Integer> normalRewardsMap = new HashMap<Integer, Integer>();
			String[] normalreward = normalrewards.split(";");
			for(String idAndCount : normalreward){
				String[] temp = idAndCount.split(":");
				String id = temp[0];
				String count = temp[1];
				normalRewardsMap.put(Integer.parseInt(id), Integer.parseInt(count));
			}
			cfgTmp.setNormalRewardsMap(normalRewardsMap);			
		}
		
		String perfectrewards = cfgTmp.getPerfectRewards();
		if(StringUtils.equals("-1", perfectrewards)){
			
		}else{
			HashMap<Integer, Integer> perfectRewardsMap = new HashMap<Integer, Integer>();
			String[] perfectReward = perfectrewards.split(";");
			for(String idAndCount : perfectReward){
				String[] temp = idAndCount.split(":");
				String id = temp[0];
				String count = temp[1];
				perfectRewardsMap.put(Integer.parseInt(id), Integer.parseInt(count));				
			}
			cfgTmp.setPerfectRewardsMap(perfectRewardsMap);
		}		
	}

	
	
	
}
