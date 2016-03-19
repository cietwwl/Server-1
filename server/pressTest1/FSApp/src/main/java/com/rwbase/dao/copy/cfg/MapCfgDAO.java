package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class MapCfgDAO extends CfgCsvDao<MapCfg> {
	private static MapCfgDAO instance = new MapCfgDAO();
	private MapCfgDAO(){}
	public static MapCfgDAO getInstance(){
		return instance;
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
