package com.rwbase.dao.skill;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.pojo.SkillListenerCfg;

/**
 * @Author HC
 * @date 2016年9月20日 下午8:15:36
 * @desc 技能监听
 **/

public class SkillListenerCfgDAO extends CfgCsvDao<SkillListenerCfg> {

	public static SkillListenerCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(SkillListenerCfgDAO.class);
	}

	@Override
	protected Map<String, SkillListenerCfg> initJsonCfg() {
		Map<String, SkillListenerCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("battle/ListenerCfg.csv", SkillListenerCfg.class);
		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, SkillListenerCfg> e : readCsv2Map.entrySet()) {
				e.getValue().initData();
			}

			cfgCacheMap = readCsv2Map;
		}
		return cfgCacheMap;
	}
}