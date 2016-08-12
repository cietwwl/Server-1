package com.rwbase.dao.sign;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.sign.pojo.SignStatisticsCfg;

public class SignStatisticsCfgDAO extends CfgCsvDao<SignStatisticsCfg>{

	public static SignStatisticsCfgDAO getInstance() {
		// TODO Auto-generated constructor stub
		return SpringContextUtil.getBean(SignStatisticsCfgDAO.class);
	}
	
	@Override
	protected Map<String, SignStatisticsCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("sign/signStatistics.csv", SignStatisticsCfg.class);
		
		return cfgCacheMap;
	}
	
	public synchronized void updateStatisticsCfg(SignStatisticsCfg cfg) {
		cfgCacheMap.put(cfg.getID(), cfg);
	} 
}
