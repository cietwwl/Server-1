package com.rwbase.dao.sign;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.sign.pojo.SignCfg;

public class SignCfgDAO extends CfgCsvDao<SignCfg> {
	public static SignCfgDAO getInstance() {
		return SpringContextUtil.getBean(SignCfgDAO.class);
	}

	@Override
	public Map<String, SignCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("sign/sign.csv",SignCfg.class);
		return cfgCacheMap;
	}
}