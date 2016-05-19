package com.playerdata.fixEquip.norm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class FixNormEquipLevelCfgDAO extends CfgCsvDao<FixNormEquipLevelCfg> {

	
	private Map<String,List<FixNormEquipLevelCfg>> parentCfgLevelMap = new HashMap<String, List<FixNormEquipLevelCfg>>();

	public static FixNormEquipLevelCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipLevelCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/norm/EquipLevelCfg.csv", FixNormEquipLevelCfg.class);
		groupByParentId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByParentId(Map<String, FixNormEquipLevelCfg> cfgCacheMap) {
	
		List<String> parentCfgList = new ArrayList<String>();
		for (FixNormEquipLevelCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getParentCfgId();
			if(!parentCfgList.contains(parentCfgId)){
				parentCfgList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : parentCfgList) {
			parentCfgLevelMap.put(pCfgId, getByParentCfgId(pCfgId));
		}
	}
	
	private List<FixNormEquipLevelCfg> getByParentCfgId(String parentCfgId){
		List<FixNormEquipLevelCfg> targetList = new ArrayList<FixNormEquipLevelCfg>();
		List<FixNormEquipLevelCfg> allCfg = getAllCfg();
		for (FixNormEquipLevelCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipLevelCfg getByParentCfgIdAndLevel(String parentCfgId, int level){
		List<FixNormEquipLevelCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixNormEquipLevelCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipLevelCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId) && tmpItem.getLevel() == level){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}