package com.rwbase.dao.guide;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.guide.pojo.GuideCfg;

public class GuideCfgDAO extends CfgCsvDao<GuideCfg> {
	private static GuideCfgDAO instance  =  new GuideCfgDAO();
	private GuideCfgDAO(){};
	public static GuideCfgDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, GuideCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("guide/guideCfg.csv",GuideCfg.class);
		return cfgCacheMap;
	}
	
	public GuideCfg getCfg(int id){
		return (GuideCfg)getCfgById(String.valueOf(id));
	}
}
