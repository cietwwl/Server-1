package com.rwbase.dao.skill;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.sign.SignCfgDAO;
import com.rwbase.dao.skill.pojo.SkillCfg;

public class SkillCfgDAO extends CfgCsvDao<SkillCfg>{ 
	
	public static SkillCfgDAO getInstance() {
		return SpringContextUtil.getBean(SkillCfgDAO.class);
	}
	
	@Override
	public Map<String, SkillCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("skillCfg/SkillCfg.csv",SkillCfg.class);
		return cfgCacheMap;
	}
	
	public SkillCfg getCfg(String skillId){
		return (SkillCfg)getCfgById(skillId);
	}
}

