package com.rwbase.dao.role;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.InlayCfg;

public class InlayCfgDAO extends CfgCsvDao<InlayCfg> {

	public static InlayCfgDAO getInstance() {
		return SpringContextUtil.getBean(InlayCfgDAO.class);
	}

	@Override
	public Map<String, InlayCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Inlay/InlayCfg.csv",InlayCfg.class);
		return cfgCacheMap;
	}
	
	public InlayCfg getConfig(String roleId){
	
		roleId=roleId.split("_")[0];
		
		List<InlayCfg> list = super.getAllCfg();
		for (InlayCfg cfg : list) {
			if(cfg.getRoleId().equals(roleId)){
				return cfg;
			}
		}
		return null;
	}

}
