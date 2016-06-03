package com.playerdata.activity.VitalityType.cfg;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class VitalitySubCfgDAO extends CfgCsvDao<VitalitySubCfg> {


	public static VitalitySubCfgDAO getInstance() {
		return SpringContextUtil.getBean(VitalitySubCfgDAO.class);
	}

	
	@Override
	public Map<String, VitalitySubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityDailyCountTypeSubCfg.csv", VitalitySubCfg.class);			
		return cfgCacheMap;
	}
	



	public VitalitySubCfg getById(String subId){
		VitalitySubCfg target = new VitalitySubCfg();
		List<VitalitySubCfg> allCfg = getAllCfg();
		for (VitalitySubCfg cfg : allCfg) {
			if(StringUtils.equals(cfg.getId(), subId)){
				target = cfg;
			}
		}
		return target;
		
	}
	
	


}