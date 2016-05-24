package com.playerdata.fixEquip.norm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixNormEquipLevelCfgDAO extends CfgCsvDao<FixNormEquipLevelCfg> {

	
	private Map<String,List<FixNormEquipLevelCfg>> planIdLevelMap = new HashMap<String, List<FixNormEquipLevelCfg>>();

	public static FixNormEquipLevelCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipLevelCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixNormEquipLevelCfg.csv", FixNormEquipLevelCfg.class);
		groupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByPlanId(Map<String, FixNormEquipLevelCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixNormEquipLevelCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getPlanId();
			if(!planIdList.contains(parentCfgId)){
				planIdList.add(parentCfgId);
			}
		}
		
		for (String planId : planIdList) {
			planIdLevelMap.put(planId, getByPlanId(planId));
		}
	}
	
	private List<FixNormEquipLevelCfg> getByPlanId(String planId){
		List<FixNormEquipLevelCfg> targetList = new ArrayList<FixNormEquipLevelCfg>();
		List<FixNormEquipLevelCfg> allCfg = getAllCfg();
		for (FixNormEquipLevelCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipLevelCfg getByPlanIdAndLevel(String planId, int level){
		List<FixNormEquipLevelCfg> allCfg = planIdLevelMap.get(planId);
		FixNormEquipLevelCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipLevelCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getLevel() == level){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}