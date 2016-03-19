package com.rwbase.dao.sign;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.sign.pojo.ReSignCfg;

public class ReSignCfgDAO extends CfgCsvDao<ReSignCfg> 
{
		private static ReSignCfgDAO instance = new ReSignCfgDAO();
		private ReSignCfgDAO() {}
		public static ReSignCfgDAO getInstance(){
			return instance;
		}

		@Override
		public Map<String, ReSignCfg> initJsonCfg() 
		{
			cfgCacheMap = CfgCsvHelper.readCsv2Map("sign/reSign.csv",ReSignCfg.class);
			return cfgCacheMap;
		}
	}
