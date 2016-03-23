package com.rwbase.dao.fresherActivity;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.FightingWeightCfgDAO;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;

/**
 * 开服活动配置表Dao
 * @author lida
 *
 */
public class FresherActivityCfgDao extends CfgCsvDao<FresherActivityCfg>{

	public static FresherActivityCfgDao getInstance() {
		return SpringContextUtil.getBean(FresherActivityCfgDao.class);
	}
	
	@Override
	public Map<String, FresherActivityCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fresherActivity/FresherActivityCfg.csv", FresherActivityCfg.class);
		return cfgCacheMap;
	}

	public FresherActivityCfg getFresherActivityCfg(int cfgId){
		FresherActivityCfg cfg = (FresherActivityCfg)cfgCacheMap.get(String.valueOf(cfgId));
		return cfg;
	}
}
