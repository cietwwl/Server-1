package com.playerdata.fixEquip.norm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixNormEquipLevelCostCfgDAO extends CfgCsvDao<FixNormEquipLevelCostCfg> {

	
	private Map<String,List<FixNormEquipLevelCostCfg>> planIdLevelMap = new HashMap<String, List<FixNormEquipLevelCostCfg>>();

	public static FixNormEquipLevelCostCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipLevelCostCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipLevelCostCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixNormEquipLevelCost.csv", FixNormEquipLevelCostCfg.class);
		groupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByPlanId(Map<String, FixNormEquipLevelCostCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixNormEquipLevelCostCfg tmpCfg : cfgCacheMap.values()) {
			String plandId = tmpCfg.getPlanId();
			if(!planIdList.contains(plandId)){
				planIdList.add(plandId);
			}
		}
		
		for (String pCfgId : planIdList) {
			planIdLevelMap.put(pCfgId, getByPlanId(pCfgId));
		}
	}
	
	private List<FixNormEquipLevelCostCfg> getByPlanId(String planId){
		List<FixNormEquipLevelCostCfg> targetList = new ArrayList<FixNormEquipLevelCostCfg>();
		List<FixNormEquipLevelCostCfg> allCfg = getAllCfg();
		for (FixNormEquipLevelCostCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipLevelCostCfg getByPlanIdAndLevel(String planId, int level){
		List<FixNormEquipLevelCostCfg> allCfg = planIdLevelMap.get(planId);
		FixNormEquipLevelCostCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipLevelCostCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getLevel() == level){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}