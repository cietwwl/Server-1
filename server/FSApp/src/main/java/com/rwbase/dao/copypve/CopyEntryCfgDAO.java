package com.rwbase.dao.copypve;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copypve.pojo.CopyEntryCfg;

public class CopyEntryCfgDAO extends CfgCsvDao<CopyEntryCfg>{

	private static CopyEntryCfgDAO instance;
	
	public static CopyEntryCfgDAO getInstance(){
		if(instance == null){
			instance = new CopyEntryCfgDAO();
		}
		return instance;
	}
	
	@Override
	public Map<String, CopyEntryCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("pve/copyEntry.csv", CopyEntryCfg.class);
		return cfgCacheMap;
	}

}
