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
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/exp/FixExpEquipLevelCfg.csv", FixExpEquipLevelCfg.class);
		groupByParentId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByParentId(Map<String, FixExpEquipLevelCfg> cfgCacheMap) {
	
		List<String> parentCfgList = new ArrayList<String>();
		for (FixExpEquipLevelCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getParentCfgId();
			if(!parentCfgList.contains(parentCfgId)){
				parentCfgList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : parentCfgList) {
			parentCfgLevelMap.put(pCfgId, getByParentCfgId(pCfgId));
		}
	}
	
	private List<FixExpEquipLevelCfg> getByParentCfgId(String parentCfgId){
		List<FixExpEquipLevelCfg> targetList = new ArrayList<FixExpEquipLevelCfg>();
		List<FixExpEquipLevelCfg> allCfg = getAllCfg();
		for (FixExpEquipLevelCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipLevelCfg getByParentCfgIdAndLevel(String parentCfgId, int level){
		List<FixExpEquipLevelCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixExpEquipLevelCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipLevelCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId) && tmpItem.getLevel() == level){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}