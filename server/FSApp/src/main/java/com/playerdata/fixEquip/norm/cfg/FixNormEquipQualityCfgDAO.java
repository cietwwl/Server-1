package com.playerdata.fixEquip.norm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixNormEquipQualityCfgDAO extends CfgCsvDao<FixNormEquipQualityCfg> {

	
	private Map<String,List<FixNormEquipQualityCfg>> parentCfgLevelMap = new HashMap<String, List<FixNormEquipQualityCfg>>();

	public static FixNormEquipQualityCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipQualityCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipQualityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixNormEquipQualityCfg.csv", FixNormEquipQualityCfg.class);
		parseNeedItemsAndGroupByPlanId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void parseNeedItemsAndGroupByPlanId(Map<String, FixNormEquipQualityCfg> cfgCacheMap) {
	
		List<String> planIdList = new ArrayList<String>();
		for (FixNormEquipQualityCfg tmpCfg : cfgCacheMap.values()) {
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
	
	private void parseNeedItems(FixNormEquipQualityCfg tmpCfg) {		
		
		Map<Integer, Integer> itemsNeed = FixEquipHelper.parseNeedItems(tmpCfg.getItemsNeedStr());		
		tmpCfg.setItemsNeed(itemsNeed);
		
	}


	private List<FixNormEquipQualityCfg> getByPlanId(String planId){
		List<FixNormEquipQualityCfg> targetList = new ArrayList<FixNormEquipQualityCfg>();
		List<FixNormEquipQualityCfg> allCfg = getAllCfg();
		for (FixNormEquipQualityCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getPlanId(), planId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipQualityCfg getByPlanIdAndQuality(String planId, int quality){
		List<FixNormEquipQualityCfg> allCfg = parentCfgLevelMap.get(planId);
		FixNormEquipQualityCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipQualityCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getPlanId(), planId) && tmpItem.getQuality() == quality){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}
	/**机器人用1个模板来获得一系列cfg,进而获得材料列表，方便gm命令添加*/
	public List<FixNormEquipQualityCfg> getGmByPlanId(String parentCfgId){
		List<FixNormEquipQualityCfg>  allCfg = parentCfgLevelMap.get(parentCfgId);
		if(allCfg == null){
			allCfg = new ArrayList<FixNormEquipQualityCfg>();
		}

		return allCfg;		
	}
}