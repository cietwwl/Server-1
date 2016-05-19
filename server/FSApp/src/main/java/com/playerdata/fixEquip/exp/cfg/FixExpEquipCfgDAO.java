package com.playerdata.fixEquip.exp.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class FixExpEquipCfgDAO extends CfgCsvDao<FixExpEquipCfg> {


	public static FixExpEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipCfgDAO.class);
	}

	
	private Map<Integer,List<FixExpEquipCfg>> heroEquipMap = new HashMap<Integer,List<FixExpEquipCfg>>();
	
	@Override
	public Map<String, FixExpEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/exp/FixExpEquipCfg.csv", FixExpEquipCfg.class);
		toHeroEquipMap(cfgCacheMap);	
		return cfgCacheMap;
	}
	
	private void toHeroEquipMap(Map<String, FixExpEquipCfg> cfgCacheMap) {
		
		Map<Integer,List<FixExpEquipCfg>>  heroEquipMapTmp = new HashMap<Integer,List<FixExpEquipCfg>>();
		for (FixExpEquipCfg cfgTmp : cfgCacheMap.values()) {
			int heroModelId = cfgTmp.getHeroModelId();
			List<FixExpEquipCfg> heroEquplist = heroEquipMapTmp.get(heroModelId);
			if(heroEquplist == null){
				heroEquplist = new ArrayList<FixExpEquipCfg>();
				heroEquipMapTmp.put(heroModelId, heroEquplist);
			}
			heroEquplist.add(cfgTmp);
		}
		
		heroEquipMap = heroEquipMapTmp;
		
	}
	
	public List<FixExpEquipCfg> getByHeroModelId(int modelId){
		return heroEquipMap.get(modelId);
	}

	public FixExpEquipCfg getConfig(String id){
		FixExpEquipCfg cfg = getCfgById(id);
		return cfg;
	}
	


}