package com.playerdata.fixEquip.exp.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.fixEquip.FixEquipHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixExpEquipQualityCfgDAO extends CfgCsvDao<FixExpEquipQualityCfg> {

	
	private Map<String,List<FixExpEquipQualityCfg>> parentCfgLevelMap = new HashMap<String, List<FixExpEquipQualityCfg>>();

	public static FixExpEquipQualityCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipQualityCfgDAO.class);
	}

	
	@Override
	public Map<String, FixExpEquipQualityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixExpEquipQualityCfg.csv", FixExpEquipQualityCfg.class);
		parseNeedItemsAndGroupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void parseNeedItemsAndGroupByPlanId(Map<String, FixExpEquipQualityCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixExpEquipQualityCfg tmpCfg : cfgCacheMap.values()) {
			parseNeedItems(tmpCfg);
			String parentCfgId = tmpCfg.getPlanId();
			if(!planIdList.contains(parentCfgId)){
				planIdList.add(parentCfgId);
			}
		}
		
		for (String planId : planIdList) {
			parentCfgLevelMap.put(planId, getByPlanId(planId));
		}
	}
	
	
	private void parseNeedItems(FixExpEquipQualityCfg tmpCfg) {
		Map<Integer, Integer> itemsNeed = FixEquipHelper.parseNeedItems(tmpCfg.getItemsNeedStr());		
		tmpCfg.setItemsNeed(itemsNeed);
	}
	
	private List<FixExpEquipQualityCfg> getByPlanId(String planId){
		List<FixExpEquipQualityCfg> targetList = new ArrayList<FixExpEquipQualityCfg>();
		List<FixExpEquipQualityCfg> allCfg = getAllCfg();
		for (FixExpEquipQualityCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipQualityCfg getByPlanIdAndQuality(String parentCfgId, int quality){
		List<FixExpEquipQualityCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixExpEquipQualityCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipQualityCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), parentCfgId) && tmpItem.getQuality() == quality){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}