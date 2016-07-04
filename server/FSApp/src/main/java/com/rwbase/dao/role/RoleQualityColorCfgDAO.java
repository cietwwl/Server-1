package com.rwbase.dao.role;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.RoleQualityColorCfg;

public class RoleQualityColorCfgDAO extends CfgCsvDao<RoleQualityColorCfg>{

	public static RoleQualityColorCfgDAO getInstante(){
		return SpringContextUtil.getBean(RoleQualityColorCfgDAO.class);
		
	}
	
	@Override
	protected Map<String, RoleQualityColorCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("role/RoleQualityColorCfg.csv",RoleQualityColorCfg.class);
		return cfgCacheMap;
	}

	public RoleQualityColorCfg getConfig(int id){
		RoleQualityColorCfg cfg = (RoleQualityColorCfg)getCfgById(String.valueOf(id));
		if(cfg != null){
			return cfg;
		}
		return null;
	}
}
