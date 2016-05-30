package com.playerdata.fixEquip.norm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
		cfgCacheMap = CfgCsvHelper.readCsv2Map("FixEquip/FixNormEquipStarCfg.csv", FixNormEquipStarCfg.class);
		groupByParentId(cfgCacheMap);
		return cfgCacheMap;
	}
	


	private void groupByParentId(Map<String, FixNormEquipStarCfg> cfgCacheMap) {
	
		List<String> parentCfgList = new ArrayList<String>();
		for (FixNormEquipStarCfg tmpCfg : cfgCacheMap.values()) {
			String parentCfgId = tmpCfg.getParentCfgId();
			if(!parentCfgList.contains(parentCfgId)){
				parentCfgList.add(parentCfgId);
			}
		}
		
		for (String pCfgId : parentCfgList) {
			parentCfgLevelMap.put(pCfgId, getByParentCfgId(pCfgId));
		}
	}
	
	private List<FixNormEquipStarCfg> getByParentCfgId(String parentCfgId){
		List<FixNormEquipStarCfg> targetList = new ArrayList<FixNormEquipStarCfg>();
		List<FixNormEquipStarCfg> allCfg = getAllCfg();
		for (FixNormEquipStarCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}


	public FixNormEquipStarCfg getByParentCfgIdAndStar(String parentCfgId, int star){
		List<FixNormEquipStarCfg> allCfg = parentCfgLevelMap.get(parentCfgId);
		FixNormEquipStarCfg target = null;
		if(allCfg!=null){
			for (FixNormEquipStarCfg tmpItem : allCfg) {
				if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId) && tmpItem.getStar() == star){
					target = tmpItem;
				}
			}
		}
		return target;
		
	}

}