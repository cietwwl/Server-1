package com.rwbase.dao.user;

import java.util.Map;

import javax.print.DocFlavor.STRING;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgBuySkillDAO extends CfgCsvDao<CfgBuySkill> {
	
	private static final String BUY_SKILL_CFG_PATH = "cfgbuySkill/cfgbuySkill.csv";
	public static CfgBuySkillDAO getInstance() {
		return SpringContextUtil.getBean(CfgBuySkillDAO.class);
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
