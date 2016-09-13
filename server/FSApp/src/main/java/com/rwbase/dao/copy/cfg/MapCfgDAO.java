package com.rwbase.dao.copy.cfg;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MapCfgDAO extends CfgCsvDao<MapCfg> {
	public static MapCfgDAO getInstance() {
		return SpringContextUtil.getBean(MapCfgDAO.class);
	}
	
	private HashMap<Integer, MapCfg> map;

	@Override
	public Map<String, MapCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/map.csv",MapCfg.class);
		HashMap<Integer, MapCfg> map = new HashMap<Integer, MapCfg>();
		for(Map.Entry<String, MapCfg> entry:cfgCacheMap.entrySet()){
			map.put(Integer.valueOf(entry.getKey()), entry.getValue());
		}
		this.map = map;
		return cfgCacheMap;
	}
	public MapCfg getCfg(int id){
//		return (MapCfg)getCfgById(String.valueOf(id));
		return this.map.get(id);
	}
	
}
