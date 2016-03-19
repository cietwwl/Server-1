package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelCfg;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;

/*
 * @author HC
 * @date 2016年2月17日 下午6:12:51
 * @Description 帮派技能等级配置DAO
 */
public class GroupSkillLevelCfgDAO extends CfgCsvDao<GroupSkillLevelCfg> {

	public static GroupSkillLevelCfgDAO getDAO() {
		return SpringContextUtil.getBean(GroupSkillLevelCfgDAO.class);
	}

	/** 帮派技能等级模版<技能Id,<技能等级,技能等级模版>> */
	private HashMap<Integer, HashMap<Integer, GroupSkillLevelTemplate>> skillLevelTmpMap = new HashMap<Integer, HashMap<Integer, GroupSkillLevelTemplate>>();
	private List<Integer> skillIdList = new ArrayList<Integer>();// 技能的Id列表

	private GroupSkillLevelCfgDAO() {
	}

	@Override
	public Map<String, GroupSkillLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/GroupSkillLevelCfg.csv", GroupSkillLevelCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			int size = cfgCacheMap.size();
			List<Integer> skillIdList = new ArrayList<Integer>();
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

				if (!skillIdList.contains(skillId)) {
					skillIdList.add(skillId);
				}
			}

			this.skillLevelTmpMap = tmpMap;
			this.skillIdList = skillIdList;
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

	/**
	 * 获取技能Id列表
	 * 
	 * @return
	 */
	public List<Integer> getAllSkillIdList() {
		return new ArrayList<Integer>(skillIdList);
	}

	/**
	 * 获取某个技能对应的技能模版列表
	 * 
	 * @param skillId
	 * @return
	 */
	public List<GroupSkillLevelTemplate> getSkillLevelTmpList(int skillId) {
		HashMap<Integer, GroupSkillLevelTemplate> hashMap = skillLevelTmpMap.get(skillId);
		if (hashMap == null) {
			return Collections.EMPTY_LIST;
		}

		return new ArrayList<GroupSkillLevelTemplate>(hashMap.values());
	}
}