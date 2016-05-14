package com.playerdata.fixEquip.exp.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
		cfgCacheMap = CfgCsvHelper.readCsv2Map("FixEquip/FixExpEquipStarCfg.csv", FixExpEquipStarCfg.class);
		groupByParentId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByParentId(Map<String, FixExpEquipStarCfg> cfgCacheMap) {
	
		List<String> parentCfgList = new ArrayList<String>();
		for (FixExpEquipStarCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getParentCfgId();
			if(!parentCfgList.contains(parentCfgId)){
				parentCfgList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : parentCfgList) {
			parentCfgLevelMap.put(pCfgId, getByParentCfgId(pCfgId));
		}
	}
	
	private List<FixExpEquipStarCfg> getByParentCfgId(String parentCfgId){
		List<FixExpEquipStarCfg> targetList = new ArrayList<FixExpEquipStarCfg>();
		List<FixExpEquipStarCfg> allCfg = getAllCfg();
		for (FixExpEquipStarCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixExpEquipStarCfg getByParentCfgIdAndStar(String parentCfgId, int star){
		List<FixExpEquipStarCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixExpEquipStarCfg target = null;
		if(allCfg!=null){
			for (FixExpEquipStarCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId) && tmpItem.getStar() == star){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}