package com.rwbase.dao.sign;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.sign.pojo.SignCfg;

public class SignCfgDAO extends CfgCsvDao<SignCfg> {
	private static SignCfgDAO instance = new SignCfgDAO();
	private SignCfgDAO() {}
	public static SignCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, SignCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("sign/sign.csv",SignCfg.class);
		return cfgCacheMap;
	}
}