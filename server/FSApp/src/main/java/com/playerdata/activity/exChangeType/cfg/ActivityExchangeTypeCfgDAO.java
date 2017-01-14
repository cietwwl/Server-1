package com.playerdata.activity.exChangeType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class ActivityExchangeTypeCfgDAO extends CfgCsvDao<ActivityExchangeTypeCfg> {
	public static ActivityExchangeTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeCfgDAO.class);
	}

	private HashMap<String, List<ActivityExchangeTypeCfg>> cfgListMap;

	@Override
	public Map<String, ActivityExchangeTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeCfg.csv", ActivityExchangeTypeCfg.class);
		HashMap<String, List<ActivityExchangeTypeCfg>> cfgListMapTmp = new HashMap<String, List<ActivityExchangeTypeCfg>>();
		for (ActivityExchangeTypeCfg cfg : cfgCacheMap.values()) {
			cfg.ExtraInitAfterLoad();
			ActivityTypeHelper.add(cfg, cfg.getEnumId(), cfgListMapTmp);
		}
		this.cfgListMap = cfgListMapTmp;
		return cfgCacheMap;
	}

	public void parseTime(ActivityExchangeTypeCfg cfg) {
		long dropStartTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getDropStartTimeStr());
		cfg.setDropStartTime(dropStartTime);
		long dropEndTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getDropEndTimeStr());
		cfg.setDropEndTime(dropEndTime);
		long changeStartTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getChangeStartTimeStr());
		cfg.setChangeStartTime(changeStartTime);
		long changeEndTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getChangeEndTimeStr());
		cfg.setChangeEndTime(changeEndTime);
	}

	public ActivityExchangeTypeCfg getCfgListByItem(ActivityExchangeTypeItem item) {
		String id = item.getCfgId();
		String enumId = item.getEnumId();
		List<ActivityExchangeTypeCfg> cfgListByItem = new ArrayList<ActivityExchangeTypeCfg>();
		ActivityExchangeTypeMgr activityExchangeTypeMgr = ActivityExchangeTypeMgr.getInstance();
		List<ActivityExchangeTypeCfg> cfgList = cfgListMap.get(enumId);
		if (cfgList == null) {
			return null;
		}
		for (ActivityExchangeTypeCfg cfg : cfgList) {
			if (!StringUtils.equals(id, String.valueOf(cfg.getId())) && activityExchangeTypeMgr.isOpen(cfg)) {
				cfgListByItem.add(cfg);
			}
		}
		if (cfgListByItem.size() > 1) {
			GameLog.error(LogModule.ComActivityExchange, null, "发现了两个以上开放的活动,活动枚举为=" + enumId, null);
			return null;
		}
		if (cfgListByItem.size() == 1) {
			return cfgListByItem.get(0);
		}

		return null;
	}

	public List<ActivityExchangeTypeCfg> isCfgByEnumIdEmpty(String enumId) {
		List<ActivityExchangeTypeCfg> typeCfgList = cfgListMap.get(enumId);
		return typeCfgList;
	}
	
	public List<ActivityExchangeTypeSubItem> newItemList(ActivityExchangeTypeCfg cfgById) {
		List<ActivityExchangeTypeSubItem> subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
		List<ActivityExchangeTypeSubCfg> subItemCfgList = ActivityExchangeTypeSubCfgDAO.getInstance().getByParentCfgId(String.valueOf(cfgById.getId()));

		if (subItemCfgList == null) {
			return subItemList;
		}
		for (ActivityExchangeTypeSubCfg activityExchangeTypeSubCfg : subItemCfgList) {
			ActivityExchangeTypeSubItem subItem = new ActivityExchangeTypeSubItem();
			subItem.setCfgId(activityExchangeTypeSubCfg.getId());
			subItem.setTime(0);
			subItem.setIsrefresh(activityExchangeTypeSubCfg.isIsrefresh());
			subItemList.add(subItem);

		}
		return subItemList;
	}

}