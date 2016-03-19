package com.rwbase.dao.user;

import java.util.Map;

import javax.print.DocFlavor.STRING;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgBuySkillDAO extends CfgCsvDao<CfgBuySkill> {
	
	private static final String BUY_SKILL_CFG_PATH = "cfgbuySkill/cfgbuySkill.csv";
	private static CfgBuySkillDAO instance = new CfgBuySkillDAO();
	private CfgBuySkillDAO() {
		
	}
	public static CfgBuySkillDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, CfgBuySkill> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(BUY_SKILL_CFG_PATH,CfgBuySkill.class);
		return cfgCacheMap;
	}
	
	public CfgBuySkill getCfgBuySkill(int times){
		if(times>getMaps().size()){
			times = cfgCacheMap.size();
		}
		CfgBuySkill cfgBuySkill = (CfgBuySkill)getCfgById(String.valueOf(times));
		return cfgBuySkill;
	}
}
