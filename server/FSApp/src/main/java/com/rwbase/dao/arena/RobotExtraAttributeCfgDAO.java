package com.rwbase.dao.arena;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.RobotExtraAttributeCfg;
import com.rwbase.dao.arena.pojo.RobotExtraAttributeTemplate;

/*
 * @author HC
 * @date 2016年7月15日 下午4:45:44
 * @Description 
 */
public class RobotExtraAttributeCfgDAO extends CfgCsvDao<RobotExtraAttributeCfg> {

	public static RobotExtraAttributeCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(RobotExtraAttributeCfgDAO.class);
	}

	private Map<Integer, RobotExtraAttributeTemplate> tmpMap;

	@Override
	protected Map<String, RobotExtraAttributeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arenaRobot/RobotExtraAttribute.csv", RobotExtraAttributeCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, RobotExtraAttributeTemplate> tmpMap = new HashMap<Integer, RobotExtraAttributeTemplate>(cfgCacheMap.size());

			for (Entry<String, RobotExtraAttributeCfg> e : cfgCacheMap.entrySet()) {
				RobotExtraAttributeCfg value = e.getValue();
				tmpMap.put(value.getExtraAttrId(), new RobotExtraAttributeTemplate(value));
			}

			this.tmpMap = Collections.unmodifiableMap(tmpMap);
		}

		return cfgCacheMap;
	}

	/**
	 * 获取额外的属性模版
	 * 
	 * @param extraId
	 * @return
	 */
	public RobotExtraAttributeTemplate getRobotExtraAttributeTemplate(int extraId) {
		if (tmpMap == null) {
			return null;
		}

		return tmpMap.get(extraId);
	}
}