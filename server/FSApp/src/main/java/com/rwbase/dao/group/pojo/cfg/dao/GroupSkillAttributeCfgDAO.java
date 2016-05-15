package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupSkillAttributeCfg;

/*
 * @author HC
 * @date 2016年3月12日 下午12:31:42
 * @Description 帮派技能属性解析
 */
public class GroupSkillAttributeCfgDAO extends CfgCsvDao<GroupSkillAttributeCfg> {

	public static GroupSkillAttributeCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSkillAttributeCfgDAO.class);
	}

	private GroupSkillAttributeCfgDAO() {
	}

	@Override
	public Map<String, GroupSkillAttributeCfg> initJsonCfg() {
		Map<String, GroupSkillAttributeCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("Group/GroupSkillAttribute.csv", GroupSkillAttributeCfg.class);

		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, GroupSkillAttributeCfg> e : readCsv2Map.entrySet()) {
				e.getValue().initData();
			}
		}

		return cfgCacheMap = readCsv2Map;
	}

	/**
	 * 获取帮派技能属性加成
	 * 
	 * @param attributeId 属性Id
	 * @return
	 */
	public GroupSkillAttributeCfg getGroupSkillAttribute(int attributeId) {
		if (cfgCacheMap == null) {
			return null;
		}

		return cfgCacheMap.get(String.valueOf(attributeId));
	}
}