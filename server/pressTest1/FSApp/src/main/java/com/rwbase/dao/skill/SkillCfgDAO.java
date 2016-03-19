package com.rwbase.dao.skill;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.pojo.SkillCfg;

public class SkillCfgDAO extends CfgCsvDao<SkillCfg>{ 
	
	private static SkillCfgDAO instance  =  new SkillCfgDAO();
	private SkillCfgDAO(){};
	public static SkillCfgDAO getInstance(){
		return instance;
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

