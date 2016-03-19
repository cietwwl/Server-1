package com.rwbase.dao.role;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.EquipAttachCfg;

public class EquipAttachCfgDAO extends CfgCsvDao<EquipAttachCfg> {

	private static EquipAttachCfgDAO instance = new EquipAttachCfgDAO();
	private EquipAttachCfgDAO(){}
	public static EquipAttachCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, EquipAttachCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("role/EquipAttachCfg.csv",EquipAttachCfg.class);
		return cfgCacheMap;
	}
	
	public EquipAttachCfg getConfig(int id){
		List<EquipAttachCfg> list = super.getAllCfg();
		for (EquipAttachCfg cfg : list) {
			if(cfg.getId() == id){
				return cfg;
			}
		}
		return null;
	}

}
