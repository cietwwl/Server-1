package com.rwbase.dao.secretArea;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.secretArea.pojo.SecretAreaCfg;

public class SecretAreaCfgDAO extends CfgCsvDao<SecretAreaCfg>{//秘境信息配置
	public static SecretAreaCfgDAO getInstance() {
		return SpringContextUtil.getBean(SecretAreaCfgDAO.class);
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
