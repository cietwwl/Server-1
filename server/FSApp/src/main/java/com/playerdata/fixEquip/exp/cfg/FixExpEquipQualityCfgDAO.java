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
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/exp/FixExpEquipQualityCfg.csv", FixExpEquipQualityCfg.class);
		parseNeedItemsAndGroupByParentId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void parseNeedItemsAndGroupByParentId(Map<String, FixExpEquipQualityCfg> cfgCacheMap) {
	
		List<String> parentCfgList = new ArrayList<String>();
		for (FixExpEquipQualityCfg tmpCfg : cfgCacheMap.values()) {
			parseNeedItems(tmpCfg);
			String parentCfgId = tmpCfg.getParentCfgId();
			if(!parentCfgList.contains(parentCfgId)){
				parentCfgList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : parentCfgList) {
			parentCfgLevelMap.put(pCfgId, getByParentCfgId(pCfgId));
		}
	}
	
	
	private void parseNeedItems(FixExpEquipQualityCfg tmpCfg) {
		Map<Integer, Integer> itemsNeed = FixEquipHelper.parseNeedItems(tmpCfg.getItemsNeedStr());		
		tmpCfg.setItemsNeed(itemsNeed);
	}
	
	private List<FixExpEquipQualityCfg> getByParentCfgId(String parentCfgId){
		List<FixExpEquipQualityCfg> targetList = new ArrayList<FixExpEquipQualityCfg>();
		List<FixExpEquipQualityCfg> allCfg = getAllCfg();
		for (FixExpEquipQualityCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipQualityCfg getByParentCfgIdAndQuality(String parentCfgId, int quality){
		List<FixExpEquipQualityCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixExpEquipQualityCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipQualityCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId) && tmpItem.getQuality() == quality){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}