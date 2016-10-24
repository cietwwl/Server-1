package com.rwbase.dao.spriteattach;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachLevelCostCfg;

public class SpriteAttachLevelCostCfgDAO extends CfgCsvDao<SpriteAttachLevelCostCfg>{

	
	private final static HashMap<Integer, HashMap<Integer, SpriteAttachLevelCostCfg>> CfgMap = new HashMap<Integer, HashMap<Integer,SpriteAttachLevelCostCfg>>();

	public static SpriteAttachLevelCostCfgDAO getInstance() {
		return SpringContextUtil.getBean(SpriteAttachLevelCostCfgDAO.class);
	}
	
	@Override
	protected Map<String, SpriteAttachLevelCostCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("SpriteAttach/SpriteAttachLevelCost.csv",SpriteAttachLevelCostCfg.class);
		
		for (Iterator<Entry<String, SpriteAttachLevelCostCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, SpriteAttachLevelCostCfg> entry = iterator.next();
			SpriteAttachLevelCostCfg cfg = entry.getValue();
			int planId = cfg.getPlanId();
			int level = cfg.getLevel();
			
			if(CfgMap.containsKey(planId)){
				HashMap<Integer, SpriteAttachLevelCostCfg> map = CfgMap.get(planId);
				map.put(level, cfg);
			}else{
				HashMap<Integer, SpriteAttachLevelCostCfg> map = new HashMap<Integer, SpriteAttachLevelCostCfg>();
				map.put(level, cfg);
				CfgMap.put(planId, map);
			}
		}
		
		return cfgCacheMap;
	}

	public SpriteAttachLevelCostCfg getSpriteAttachLevelCost(int level, int planId){
		HashMap<Integer,SpriteAttachLevelCostCfg> map = CfgMap.get(planId);
		if(map!= null){
			return map.get(level);
		}
		return null;
	}
}
