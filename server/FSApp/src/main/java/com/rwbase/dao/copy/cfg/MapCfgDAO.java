package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MapCfgDAO extends CfgCsvDao<MapCfg> {
	public static MapCfgDAO getInstance() {
		return SpringContextUtil.getBean(MapCfgDAO.class);
	}

	@Override
	public Map<String, MapCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/map.csv",MapCfg.class);
		return cfgCacheMap;
	}
	public MapCfg getCfg(int id){
		return (MapCfg)getCfgById(String.valueOf(id));
	}
	
}
