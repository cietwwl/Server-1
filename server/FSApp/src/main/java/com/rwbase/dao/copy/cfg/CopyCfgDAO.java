package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CopyCfgDAO extends CfgCsvDao<CopyCfg>{

	public static CopyCfgDAO getInstance() {
		return SpringContextUtil.getBean(CopyCfgDAO.class);
	}
	
	@Override
	public Map<String, CopyCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/copy.csv",CopyCfg.class);
		return cfgCacheMap;
	}
	public CopyCfg getCfg(int id){
		return (CopyCfg)getCfgById(String.valueOf(id));
	}
}
