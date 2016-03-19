package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelCfg;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;

/*
 * @author HC
 * @date 2016年2月17日 下午6:12:51
 * @Description 帮派技能等级配置DAO
 */
public class GroupSkillLevelCfgDAO extends CfgCsvDao<GroupSkillLevelCfg> {

	private static GroupSkillLevelCfgDAO dao = new GroupSkillLevelCfgDAO();

	public static GroupSkillLevelCfgDAO getDAO() {
		return dao;
	}

	/** 帮派技能等级模版<技能Id,<技能等级,技能等级模版>> */
	HashMap<Integer, HashMap<Integer, GroupSkillLevelTemplate>> skillLevelTmpMap = new HashMap<Integer, HashMap<Integer, GroupSkillLevelTemplate>>();

	private GroupSkillLevelCfgDAO() {
	}

	@Override
	public Map<String, GroupSkillLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("", GroupSkillLevelCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			int size = cfgCacheMap.size();
			HashMap<Integer, HashMap<Integer, GroupSkillLevelTemplate>> tmpMap = new HashMap<Integer, HashMap<Integer, GroupSkillLevelTemplate>>(size);
			for (Entry<String, GroupSkillLevelCfg> e : cfgCacheMap.entrySet()) {
				GroupSkillLevelCfg gslc = e.getValue();

				int skillId = gslc.getSkillId();// 技能Id
				HashMap<Integer, GroupSkillLevelTemplate> map = tmpMap.get(skillId);
				if (map == null) {
					map = new HashMap<Integer, GroupSkillLevelTemplate>();
					tmpMap.put(skillId, map);
				}

				GroupSkillLevelTemplate gslt = new GroupSkillLevelTemplate(gslc);
				map.put(gslc.getSkillLevel(), gslt);
			}

			skillLevelTmpMap = tmpMap;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取技能的等级配置表
	 * 
	 * @param skillId 技能的Id
	 * @param level 技能的等级
	 * @return
	 */
	public GroupSkillLevelTemplate getSkillLevelTemplate(int skillId, int level) {
		if (skillLevelTmpMap.isEmpty()) {
			return null;
		}

		HashMap<Integer, GroupSkillLevelTemplate> levelMap = skillLevelTmpMap.get(skillId);
		if (levelMap == null || levelMap.isEmpty()) {
			return null;
		}

		return levelMap.get(level);
	}

	/**
	 * 获取某个技能的最大等级
	 * 
	 * @param skillId
	 * @return
	 */
	public int getSkillMaxLevel(int skillId) {
		if (skillLevelTmpMap.isEmpty()) {
			return 0;
		}

		HashMap<Integer, GroupSkillLevelTemplate> levelMap = skillLevelTmpMap.get(skillId);
		if (levelMap == null || levelMap.isEmpty()) {
			return 0;
		}

		return levelMap.size();
	}
}