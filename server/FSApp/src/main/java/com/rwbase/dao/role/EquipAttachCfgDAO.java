package com.rwbase.dao.role;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.ranking.CfgRankingDAO;
import com.rwbase.dao.role.pojo.EquipAttachCfg;

public class EquipAttachCfgDAO extends CfgCsvDao<EquipAttachCfg> {

	public static EquipAttachCfgDAO getInstance() {
		return SpringContextUtil.getBean(EquipAttachCfgDAO.class);
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
