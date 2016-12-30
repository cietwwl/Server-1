package com.playerdata.battleVerify.damageControll;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public class DamageControllCfgDAO extends CfgCsvDao<DamageControllCfg>{

	public static DamageControllCfgDAO getInstance(){
		return SpringContextUtil.getBean(DamageControllCfgDAO.class);
	}
	
	
	@Override
	protected Map<String, DamageControllCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battle/damageControllCfg", DamageControllCfg.class);
		return cfgCacheMap;
	}

}
