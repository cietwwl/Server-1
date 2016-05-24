package com.playerdata.fixEquip.exp.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixExpEquipLevelCfgDAO extends CfgCsvDao<FixExpEquipLevelCfg> {

	
	private Map<String,List<FixExpEquipLevelCfg>> parentCfgLevelMap = new HashMap<String, List<FixExpEquipLevelCfg>>();

	public static FixExpEquipLevelCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipLevelCfgDAO.class);
	}

	
	@Override
	public Map<String, FixExpEquipLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixExpEquipLevelCfg.csv", FixExpEquipLevelCfg.class);
		groupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByPlanId(Map<String, FixExpEquipLevelCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixExpEquipLevelCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getPlanId();
			if(!planIdList.contains(parentCfgId)){
				planIdList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : planIdList) {
			parentCfgLevelMap.put(pCfgId, getByPlanId(pCfgId));
		}
	}
	
	private List<FixExpEquipLevelCfg> getByPlanId(String planId){
		List<FixExpEquipLevelCfg> targetList = new ArrayList<FixExpEquipLevelCfg>();
		List<FixExpEquipLevelCfg> allCfg = getAllCfg();
		for (FixExpEquipLevelCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipLevelCfg getByPlanIdAndLevel(String planId, int level){
		List<FixExpEquipLevelCfg> allCfg = parentCfgLevelMap.get(planId);
		FixExpEquipLevelCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipLevelCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getLevel() == level){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}