package com.rwbase.dao.user;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.user.pojo.LevelCfg;

public class LevelCfgDAO extends CfgCsvDao<LevelCfg> {
	
	private static LevelCfgDAO instance  =  new LevelCfgDAO();
	private LevelCfgDAO(){};
	public static LevelCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, LevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("LevelCfg/LevelCfg.csv",LevelCfg.class);
		return cfgCacheMap;
	}
	
	public LevelCfg getByLevel(int level){
		return (LevelCfg)super.getCfgById(""+level);
	}
}
