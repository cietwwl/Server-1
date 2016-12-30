package com.playerdata.activity.exChangeType.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.eSpecialItemId;

public final class ActivityExchangeTypeSubCfgDAO extends CfgCsvDao<ActivityExchangeTypeSubCfg> {

	public static ActivityExchangeTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeSubCfgDAO.class);
	}

	private HashMap<String, List<ActivityExchangeTypeSubCfg>> subCfgListMap;

	@Override
	public Map<String, ActivityExchangeTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeSubCfg.csv", ActivityExchangeTypeSubCfg.class);
		for (ActivityExchangeTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		HashMap<String, List<ActivityExchangeTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityExchangeTypeSubCfg>>();
		for (ActivityExchangeTypeSubCfg subCfg : cfgCacheMap.values()) {
			ActivityTypeHelper.add(subCfg, String.valueOf(subCfg.getParentCfg()), subCfgListMapTmp);
		}
		this.subCfgListMap = subCfgListMapTmp;
		return cfgCacheMap;
	}

	/** 解析a_a1,b_b1 */
	private void parseTime(ActivityExchangeTypeSubCfg cfgTmp) {
		HashMap<Integer, Integer> changelisttmp = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> eSpecialItemchangelisttmp = new HashMap<Integer, Integer>();

		if (StringUtils.isBlank(cfgTmp.getExchangeneed())) {
			GameLog.error(LogModule.ComActivityExchange, "", "配置文件没有exchangeneed字段", null);
			return;
		}
		String[] changeStrs = cfgTmp.getExchangeneed().split(",");
		for (String tmp : changeStrs) {
			String[] Strs = tmp.split("_");
			Integer id = Integer.parseInt(Strs[0]);
			Integer count = Integer.parseInt(Strs[1]);
			if (id < eSpecialItemId.eSpecial_End.getValue()) {
				eSpecialItemchangelisttmp.put(id, count);
			} else {
				changelisttmp.put(id, count);
			}
		}
		cfgTmp.setChangelist(changelisttmp);
		cfgTmp.seteSpecialItemChangeList(eSpecialItemchangelisttmp);
	}

	public List<ActivityExchangeTypeSubCfg> getByParentCfgId(String parentCfgId) {
		return subCfgListMap.get(parentCfgId);
	}

	public ActivityExchangeTypeSubCfg getById(String cfgId) {
		ActivityExchangeTypeSubCfg cfg = null;
		List<ActivityExchangeTypeSubCfg> cfglist = getAllCfg();
		for (ActivityExchangeTypeSubCfg subcfg : cfglist) {
			if (StringUtils.equals(String.valueOf(subcfg.getId()), cfgId)) {
				cfg = subcfg;
				break;
			}
		}
		return cfg;
	}

}