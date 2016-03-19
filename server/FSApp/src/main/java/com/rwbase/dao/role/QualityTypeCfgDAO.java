package com.rwbase.dao.role;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.QualityTypeCfg;

public class QualityTypeCfgDAO  extends CfgCsvDao<QualityTypeCfg> {
	private static QualityTypeCfgDAO instance = new QualityTypeCfgDAO();
	private QualityTypeCfgDAO(){}
	public static QualityTypeCfgDAO getInstance(){ 
		return instance;
	}

	@Override
	public Map<String, QualityTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("equip/QualityTypeCfg.csv",QualityTypeCfg.class);
		return cfgCacheMap;
	}
	
	public QualityTypeCfg getConfig(int id){
		return (QualityTypeCfg)getCfgById(String.valueOf(id));
	}

	public int getAttachId(int id){
		QualityTypeCfg cfg = (QualityTypeCfg)getCfgById(String.valueOf(id));
		if(cfg != null){
			return cfg.getAttachId();
		}
		return 0;
	}
	
	public int getMaxAttachLevel(int id){
		QualityTypeCfg cfg = (QualityTypeCfg)getCfgById(String.valueOf(id));
		if(cfg != null){
			return cfg.getMaxAttachLevel();
		}
		return 0;
	}
}
