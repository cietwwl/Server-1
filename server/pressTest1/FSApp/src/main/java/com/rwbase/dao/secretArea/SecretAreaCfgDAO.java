package com.rwbase.dao.secretArea;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.secretArea.pojo.SecretAreaCfg;

public class SecretAreaCfgDAO extends CfgCsvDao<SecretAreaCfg>{//秘境信息配置
	private static SecretAreaCfgDAO instance = new SecretAreaCfgDAO();
	private SecretAreaCfgDAO() {
		
	}
	
	public static SecretAreaCfgDAO getInstance(){
		return instance;
	}
	
	public Map<String, SecretAreaCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("secretArea/secretCfg.csv",SecretAreaCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public SecretAreaCfg getSecretCfg(int type){
		return (SecretAreaCfg)getCfgById(String.valueOf(type+1));
	}
}
