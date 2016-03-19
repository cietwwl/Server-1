package com.rwbase.dao.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.ComposeCfg;

public class ComposeCfgDAO extends CfgCsvDao<ComposeCfg> {
	private static ComposeCfgDAO instance = new ComposeCfgDAO();
	private ComposeCfgDAO(){}
	public static ComposeCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, ComposeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/ComposeCfg.csv",ComposeCfg.class);
		return cfgCacheMap;
	}
	public ComposeCfg getCfg(int id){
		return  (ComposeCfg)getCfgById(String.valueOf(id));
	}
	
	public HashMap<Integer,Integer> getMate(int id){
		HashMap<Integer,Integer> mate = new HashMap<Integer, Integer>(); 
		ComposeCfg cfg = getCfg(id);
		if(cfg == null)return null;
		if (cfg.getMate1Id() > 0) {
			mate.put(cfg.getMate1Id(), cfg.getMate1Count());
		}
		if (cfg.getMate2Id() > 0) {
			mate.put(cfg.getMate2Id(), cfg.getMate2Count());
		}
		if (cfg.getMate3Id() > 0) {
			mate.put(cfg.getMate3Id(), cfg.getMate3Count());
		}
		if (cfg.getMate4Id() > 0) {
			mate.put(cfg.getMate4Id(), cfg.getMate4Count());
		}
		if (cfg.getMate5Id() > 0) {
			mate.put(cfg.getMate5Id(), cfg.getMate5Count());
		}
		return mate;
	}
	
	public ComposeCfg GetItemComposeCfg(int mateId){
		List<ComposeCfg> list = super.getAllCfg();
		for (ComposeCfg cfg : list) {
			if(cfg.getMate1Id() == mateId && cfg.getComposeType() == 0){
				return cfg;
			}
		}
		return null;
	}
}
