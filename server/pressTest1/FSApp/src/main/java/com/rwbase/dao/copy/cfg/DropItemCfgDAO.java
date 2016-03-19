package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rwbase.common.config.CfgCsvHelper;

public class DropItemCfgDAO extends CfgCsvDao<DropItemCfg>{

	public static DropItemCfgDAO instance = new DropItemCfgDAO();
	private DropItemCfgDAO() {}
	public static DropItemCfgDAO getInstance(){
		return instance;
	}
			
	@Override
	public Map<String, DropItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/DropItem.csv",DropItemCfg.class);
		return cfgCacheMap;
	}

}