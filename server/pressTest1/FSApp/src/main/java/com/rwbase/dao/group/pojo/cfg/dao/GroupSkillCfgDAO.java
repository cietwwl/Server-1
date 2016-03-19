package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupSkillCfg;

/*
 * @author HC
 * @date 2016年2月17日 下午6:12:36
 * @Description 帮派技能配置DAO
 */
public class GroupSkillCfgDAO extends CfgCsvDao<GroupSkillCfg> {
	private static GroupSkillCfgDAO dao = new GroupSkillCfgDAO();

	public static GroupSkillCfgDAO getDAO() {
		return dao;
	}

	private GroupSkillCfgDAO() {
	}

	@Override
	public Map<String, GroupSkillCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("", GroupSkillCfg.class);
		return cfgCacheMap;
	}

	/**
	 * 获取技能的配置表
	 * 
	 * @param skillId
	 * @return
	 */
	public GroupSkillCfg getSkillCfg(int skillId) {
		if (cfgCacheMap == null || cfgCacheMap.isEmpty()) {
			return null;
		}

		return cfgCacheMap.get(String.valueOf(skillId));
	}
}