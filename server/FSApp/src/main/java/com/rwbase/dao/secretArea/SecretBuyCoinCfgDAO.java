package com.rwbase.dao.secretArea;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.secretArea.pojo.SecretBuyCoinCfg;

public class SecretBuyCoinCfgDAO extends CfgCsvDao<SecretBuyCoinCfg>{//购买密钥花费配置
	public static SecretBuyCoinCfgDAO getInstance() {
		return SpringContextUtil.getBean(SecretBuyCoinCfgDAO.class);
	}
	
	public Map<String, SecretBuyCoinCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("secretArea/buySecretCoin.csv",SecretBuyCoinCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public SecretBuyCoinCfg getSecretCfg(int times){
		if(times>getMaps().size()){
			times = cfgCacheMap.size();
		}
		return (SecretBuyCoinCfg)getCfgById(String.valueOf(times));
	}

}
