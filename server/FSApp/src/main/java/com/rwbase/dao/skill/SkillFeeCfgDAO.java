package com.rwbase.dao.skill;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.pojo.SkillFeeCfg;

public class SkillFeeCfgDAO extends CfgCsvDao<SkillFeeCfg> {
	
	private static SkillFeeCfgDAO instance  =  new SkillFeeCfgDAO();
	private SkillFeeCfgDAO(){};
	public static SkillFeeCfgDAO getInstance(){
		return instance;
	}
	//isPlayer+"_"+order+"_"+level
	public SkillFeeCfg getSkillFeeCfg(int isPlayer,int order,int level){
		return (SkillFeeCfg)getCfgById(isPlayer+"_"+order+"_"+level);
	}
	
	@Override
	public Map<String, SkillFeeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("skillCfg/SkillFeeCfg.csv",SkillFeeCfg.class);
		return cfgCacheMap;
	}
}

