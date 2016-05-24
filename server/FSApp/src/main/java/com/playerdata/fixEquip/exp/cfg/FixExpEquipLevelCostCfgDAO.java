package com.playerdata.fixEquip.exp.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixExpEquipLevelCostCfgDAO extends CfgCsvDao<FixExpEquipLevelCostCfg> {

	
	private Map<String,List<FixExpEquipLevelCostCfg>> parentCfgLevelMap = new HashMap<String, List<FixExpEquipLevelCostCfg>>();

	public static FixExpEquipLevelCostCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipLevelCostCfgDAO.class);
	}

	
	@Override
	public Map<String, FixExpEquipLevelCostCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixExpEquipLevelCost.csv", FixExpEquipLevelCostCfg.class);
		groupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByPlanId(Map<String, FixExpEquipLevelCostCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixExpEquipLevelCostCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getPlanId();
			if(!planIdList.contains(parentCfgId)){
				planIdList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : planIdList) {
			parentCfgLevelMap.put(pCfgId, getByPlanId(pCfgId));
		}
	}
	
	private List<FixExpEquipLevelCostCfg> getByPlanId(String planId){
		List<FixExpEquipLevelCostCfg> targetList = new ArrayList<FixExpEquipLevelCostCfg>();
		List<FixExpEquipLevelCostCfg> allCfg = getAllCfg();
		for (FixExpEquipLevelCostCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipLevelCostCfg getByPlanIdAndLevel(String planId, int level){
		List<FixExpEquipLevelCostCfg> allCfg = parentCfgLevelMap.get(planId);
		FixExpEquipLevelCostCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipLevelCostCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getLevel() == level){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}