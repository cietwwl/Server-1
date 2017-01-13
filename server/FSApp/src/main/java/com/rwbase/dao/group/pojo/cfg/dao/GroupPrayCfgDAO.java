package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupPrayCfg;

/**
 * @Author HC
 * @date 2016年12月22日 下午8:15:02
 * @desc 帮派祈福的配置DAO
 **/

public class GroupPrayCfgDAO extends CfgCsvDao<GroupPrayCfg> {

	public static GroupPrayCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupPrayCfgDAO.class);
	}

	private HashMap<Integer, Integer> getSoulLimitMap;// 祈福中获取魂石的上限

	@Override
	protected Map<String, GroupPrayCfg> initJsonCfg() {
		Map<String, GroupPrayCfg> map = CfgCsvHelper.readCsv2Map("GroupPray/GroupPrayLimitCfg.csv", GroupPrayCfg.class);

		if (map != null && !map.isEmpty()) {
			HashMap<Integer, Integer> getSoulLimitMap = new HashMap<Integer, Integer>(map.size());
			for (Entry<String, GroupPrayCfg> e : map.entrySet()) {
				GroupPrayCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int soulModelId = cfg.getTargetSoulItemId();
				Integer hasValue = getSoulLimitMap.get(soulModelId);
				if (hasValue == null) {
					getSoulLimitMap.put(soulModelId, cfg.getExchangeLimit());
				}
			}

			this.getSoulLimitMap = getSoulLimitMap;
		}

		return cfgCacheMap = map;
	}

	/**
	 * 获取每天可以从祈福中获取的魂石的数量
	 * 
	 * @param soulModelId 魂石的Id
	 * @return
	 */
	public int getSoulLimit(int soulModelId) {
		if (getSoulLimitMap == null || getSoulLimitMap.isEmpty()) {
			return 0;
		}

		Integer hasValue = getSoulLimitMap.get(soulModelId);
		return hasValue == null ? 0 : hasValue.intValue();
	}
}