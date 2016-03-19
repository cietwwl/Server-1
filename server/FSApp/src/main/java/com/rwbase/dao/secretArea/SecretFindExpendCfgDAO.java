
package com.rwbase.dao.secretArea;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.secretArea.pojo.SecretFindExpendCfg;

public class SecretFindExpendCfgDAO extends CfgCsvDao<SecretFindExpendCfg>{//查找敌方 花费
	private static SecretFindExpendCfgDAO instance = new SecretFindExpendCfgDAO();
	private SecretFindExpendCfgDAO() {
		
	}
	
	public static SecretFindExpendCfgDAO getInstance(){
		return instance;
	}
	
	public Map<String, SecretFindExpendCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("secretArea/findSecretExpend.csv",SecretFindExpendCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public SecretFindExpendCfg getSecretCfg(int times){
		if(times>getMaps().size()){
			times = getMaps().size();
		}
		return (SecretFindExpendCfg)getCfgById(String.valueOf(times));
	}

}
