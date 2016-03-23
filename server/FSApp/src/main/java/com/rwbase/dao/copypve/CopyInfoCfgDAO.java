package com.rwbase.dao.copypve;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;

public class CopyInfoCfgDAO extends CfgCsvDao<CopyInfoCfg> {

	public static CopyInfoCfgDAO getInstance() {
		return SpringContextUtil.getBean(CopyInfoCfgDAO.class);
	}
	
	
	@Override
	public Map<String, CopyInfoCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("pve/copyInfo.csv", CopyInfoCfg.class);
		return cfgCacheMap;
	}

}
