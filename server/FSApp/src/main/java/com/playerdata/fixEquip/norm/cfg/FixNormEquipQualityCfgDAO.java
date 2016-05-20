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

public final class FixNormEquipQualityCfgDAO extends CfgCsvDao<FixNormEquipQualityCfg> {

	
	private Map<String,List<FixNormEquipQualityCfg>> parentCfgLevelMap = new HashMap<String, List<FixNormEquipQualityCfg>>();

	public static FixNormEquipQualityCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipQualityCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipQualityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/norm/FixNormEquipQualityCfg.csv", FixNormEquipQualityCfg.class);
		parseNeedItemsAndGroupByParentId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void parseNeedItemsAndGroupByParentId(Map<String, FixNormEquipQualityCfg> cfgCacheMap) {
	
		List<String> parentCfgList = new ArrayList<String>();
		for (FixNormEquipQualityCfg tmpCfg : cfgCacheMap.values()) {
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
	
	private void parseNeedItems(FixNormEquipQualityCfg tmpCfg) {		
		
		Map<Integer, Integer> itemsNeed = FixEquipHelper.parseNeedItems(tmpCfg.getItemsNeedStr());		
		tmpCfg.setItemsNeed(itemsNeed);
		
	}


	private List<FixNormEquipQualityCfg> getByParentCfgId(String parentCfgId){
		List<FixNormEquipQualityCfg> targetList = new ArrayList<FixNormEquipQualityCfg>();
		List<FixNormEquipQualityCfg> allCfg = getAllCfg();
		for (FixNormEquipQualityCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipQualityCfg getByParentCfgIdAndQuality(String parentCfgId, int quality){
		List<FixNormEquipQualityCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixNormEquipQualityCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipQualityCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId) && tmpItem.getQuality() == quality){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}