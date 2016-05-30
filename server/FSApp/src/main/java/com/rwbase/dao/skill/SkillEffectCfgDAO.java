package com.rwbase.dao.skill;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.pojo.SkillEffectCfg;

/*
 * @author HC
 * @date 2016年5月2日 下午12:45:59
 * @Description 
 */
public class SkillEffectCfgDAO extends CfgCsvDao<SkillEffectCfg> {
	public static SkillEffectCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(SkillEffectCfgDAO.class);
	}

	public SkillEffectCfgDAO() {
	}

	@Override
	protected Map<String, SkillEffectCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battle/skillEffect.csv", SkillEffectCfg.class);
		return cfgCacheMap;
	}
}