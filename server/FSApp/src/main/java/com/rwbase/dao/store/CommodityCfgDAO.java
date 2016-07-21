package com.rwbase.dao.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.SkillFeeCfgDAO;
import com.rwbase.dao.store.pojo.CommodityCfg;

public class CommodityCfgDAO extends CfgCsvDao<CommodityCfg> {
	public static CommodityCfgDAO getInstance() {
		return SpringContextUtil.getBean(CommodityCfgDAO.class);
	}
	@Override
	public Map<String, CommodityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("store/CommodityCfg.csv",CommodityCfg.class);
		return cfgCacheMap;
	}
	
	public List<CommodityCfg> GetCommdity(int storeId,int type,int level){
		List<CommodityCfg> map = new ArrayList<CommodityCfg>();
		List<CommodityCfg> all = super.getAllCfg();
		for (CommodityCfg cfg : all) {
			if(cfg.getStoreId() == storeId && cfg.getType() == type){
				String[] levelZone = cfg.getLevel().split("_");
				if(Integer.parseInt(levelZone[0]) <= level && Integer.parseInt(levelZone[1]) >= level ){
					map.add(cfg);
				}
			}
		}
		return map;
	}

	public CommodityCfg GetCommodityCfg(int id){
		for (CommodityCfg cfg : super.getAllCfg()) {
			if(cfg.getId() == id){
				return cfg;
			}
		}
		return null;
	}
}
