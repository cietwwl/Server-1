package com.rwbase.dao.sign;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.setting.HeadCfgDAO;
import com.rwbase.dao.sign.pojo.ReSignCfg;

public class ReSignCfgDAO extends CfgCsvDao<ReSignCfg> 
{
		public static ReSignCfgDAO getInstance() {
			return SpringContextUtil.getBean(ReSignCfgDAO.class);
		}

		@Override
		public Map<String, ReSignCfg> initJsonCfg() 
		{
			cfgCacheMap = CfgCsvHelper.readCsv2Map("sign/reSign.csv",ReSignCfg.class);
			return cfgCacheMap;
		}
	}
