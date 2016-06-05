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

public final class FixExpEquipStarCfgDAO extends CfgCsvDao<FixExpEquipStarCfg> {

	
	private Map<String,List<FixExpEquipStarCfg>> parentCfgLevelMap = new HashMap<String, List<FixExpEquipStarCfg>>();

	public static FixExpEquipStarCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipStarCfgDAO.class);
	}

	
	@Override
	public Map<String, FixExpEquipStarCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixExpEquipStarCfg.csv", FixExpEquipStarCfg.class);
		parseNeedItemsAndGroupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void parseNeedItemsAndGroupByPlanId(Map<String, FixExpEquipStarCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixExpEquipStarCfg tmpCfg : cfgCacheMap.values()) {
			tmpCfg.initData();
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
	
	private void parseNeedItems(FixExpEquipStarCfg tmpCfg) {
		Map<Integer, Integer> itemsNeed = FixEquipHelper.parseNeedItems(tmpCfg.getItemsNeedStr());		
		tmpCfg.setItemsNeed(itemsNeed);
	}
	
	private List<FixExpEquipStarCfg> getByPlanId(String planId){
		List<FixExpEquipStarCfg> targetList = new ArrayList<FixExpEquipStarCfg>();
		List<FixExpEquipStarCfg> allCfg = getAllCfg();
		for (FixExpEquipStarCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipStarCfg getByPlanIdAndStar(String planId, int star){
		List<FixExpEquipStarCfg> allCfg = parentCfgLevelMap.get(planId);
		FixExpEquipStarCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipStarCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getStar() == star){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}