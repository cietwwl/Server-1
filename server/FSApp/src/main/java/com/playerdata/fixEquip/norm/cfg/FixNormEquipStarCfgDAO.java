package com.playerdata.fixEquip.norm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.fixEquip.FixEquipHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixNormEquipStarCfgDAO extends CfgCsvDao<FixNormEquipStarCfg> {

	
	private Map<String,List<FixNormEquipStarCfg>> parentCfgLevelMap = new HashMap<String, List<FixNormEquipStarCfg>>();

	public static FixNormEquipStarCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipStarCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipStarCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixNormEquipStarCfg.csv", FixNormEquipStarCfg.class);
		parseNeedItemsAndGroupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void parseNeedItemsAndGroupByPlanId(Map<String, FixNormEquipStarCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixNormEquipStarCfg tmpCfg : cfgCacheMap.values()) {
			parseNeedItems(tmpCfg);
			String planId = tmpCfg.getPlanId();
			if(!planIdList.contains(planId)){
				planIdList.add(planId);
			}
		}
		
		for (String planId : planIdList) {
			parentCfgLevelMap.put(planId, getByPlanId(planId));
		}
	}
	
	private void parseNeedItems(FixNormEquipStarCfg tmpCfg) {
		Map<Integer, Integer> itemsNeed = FixEquipHelper.parseNeedItems(tmpCfg.getItemsNeedStr());		
		tmpCfg.setItemsNeed(itemsNeed);
	}
	
	private List<FixNormEquipStarCfg> getByPlanId(String parentCfgId){
		List<FixNormEquipStarCfg> targetList = new ArrayList<FixNormEquipStarCfg>();
		List<FixNormEquipStarCfg> allCfg = getAllCfg();
		for (FixNormEquipStarCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipStarCfg getByPlanIdAndStar(String planId, int star){
		List<FixNormEquipStarCfg> allCfg = parentCfgLevelMap.get(planId);
		FixNormEquipStarCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipStarCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getStar() == star){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}