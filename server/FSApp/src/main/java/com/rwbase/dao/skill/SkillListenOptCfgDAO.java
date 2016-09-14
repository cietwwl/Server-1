package com.rwbase.dao.skill;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.pojo.SkillListenOptCfg;
import com.rwbase.dao.skill.pojo.SkillListenOptTemplate;

/**
 * @Author HC
 * @date 2016年8月23日 下午5:15:14
 * @desc
 **/

public class SkillListenOptCfgDAO extends CfgCsvDao<SkillListenOptCfg> {

	public static SkillListenOptCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(SkillListenOptCfgDAO.class);
	}

	private Map<Integer, SkillListenOptTemplate> skillListenMap;

	@Override
	protected Map<String, SkillListenOptCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("skillCfg/SkillListenOpt.csv", SkillListenOptCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, SkillListenOptTemplate> skillListenMap = new HashMap<Integer, SkillListenOptTemplate>();

			for (Entry<String, SkillListenOptCfg> e : cfgCacheMap.entrySet()) {
				SkillListenOptCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				skillListenMap.put(cfg.getOptId(), new SkillListenOptTemplate(cfg));
			}

			if (skillListenMap.isEmpty()) {
				this.skillListenMap = Collections.emptyMap();
			} else {
				this.skillListenMap = Collections.unmodifiableMap(skillListenMap);
			}
		}

		return cfgCacheMap;
	}

	/**
	 * 获取技能监听的事件
	 * 
	 * @param optId
	 * @return
	 */
	public SkillListenOptTemplate getSkillListenOptTemplate(int optId) {
		if (skillListenMap == null || skillListenMap.isEmpty()) {
			return null;
		}

		return skillListenMap.get(optId);
	}
}