package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class DropItemCfgDAO extends CfgCsvDao<DropItemCfg>{

	public static DropItemCfgDAO getInstance() {
		return SpringContextUtil.getBean(DropItemCfgDAO.class);
	}
			
	@Override
	public Map<String, DropItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/DropItem.csv",DropItemCfg.class);
		return cfgCacheMap;
	}

}