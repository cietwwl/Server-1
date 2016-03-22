package com.rwbase.dao.copypve;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copy.cfg.MapCfgDAO;
import com.rwbase.dao.copypve.pojo.CopyEntryCfg;

public class CopyEntryCfgDAO extends CfgCsvDao<CopyEntryCfg>{

	public static CopyEntryCfgDAO getInstance() {
		return SpringContextUtil.getBean(CopyEntryCfgDAO.class);
	}
	
	@Override
	public Map<String, CopyEntryCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("pve/copyEntry.csv", CopyEntryCfg.class);
		return cfgCacheMap;
	}

}
