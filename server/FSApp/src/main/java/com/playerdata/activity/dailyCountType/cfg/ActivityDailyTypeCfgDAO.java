package com.playerdata.activity.dailyCountType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityDailyTypeCfgDAO extends
		CfgCsvDao<ActivityDailyTypeCfg> {
	public static ActivityDailyTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyTypeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityDailyTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityDailyCountTypeCfg.csv",
				ActivityDailyTypeCfg.class);
		for (ActivityDailyTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		return cfgCacheMap;
	}

	private void parseTime(ActivityDailyTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem
				.getStartTimeStr());
		cfgItem.setStartTime(startTime);

		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem
				.getEndTimeStr());
		cfgItem.setEndTime(endTime);
	}

	public ActivityDailyTypeCfg getConfig(String id) {
		ActivityDailyTypeCfg cfg = getCfgById(id);
		return cfg;
	}

	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum
	 *            每日重置类型的活动,第几天
	 * @return
	 */
	public ActivityDailyTypeItem newItem(Player player, ActivityDailyTypeCfg cfg) {
		if (cfg != null) {
			ActivityDailyTypeItem item = new ActivityDailyTypeItem();
			item.setId(player.getUserId());
			item.setUserId(player.getUserId());
			item.setCfgid(cfg.getId());
			item.setVersion(cfg.getVersion());
			item.setSubItemList(newItemList(cfg.getId()));
			item.setLastTime(System.currentTimeMillis());
			return item;
		} else {
			return null;
		}
	}

	public List<ActivityDailyTypeSubItem> newItemList(String parentid) {
		ActivityDailyTypeSubCfgDAO activityDailyTypeSubCfgDAO = ActivityDailyTypeSubCfgDAO.getInstance();
				
		
		List<ActivityDailyTypeSubItem> subItemList = new ArrayList<ActivityDailyTypeSubItem>();
		List<ActivityDailyTypeSubCfg> subCfgListByParentid = ActivityDailyTypeSubCfgDAO
				.getInstance().getCfgMapByParentid(parentid);
		for (ActivityDailyTypeSubCfg subCfg : subCfgListByParentid) {
			if (!activityDailyTypeSubCfgDAO.isOpen(subCfg)) {
				// 该子类型活动当天没开启
				continue;
			}
			ActivityDailyTypeSubItem subitem = new ActivityDailyTypeSubItem();
			subitem.setCfgId(subCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(subCfg.getGiftId());
			subItemList.add(subitem);
		}
		return subItemList;
	}

	/**
	 * 
	 * @param targetItem
	 * @return 根据传入的item，获取同类型的，不同id的，激活的，唯一的下期配置
	 */
	public ActivityDailyTypeCfg getCfgByItemOfItemId(
			ActivityDailyTypeItem targetItem) {
		String id = targetItem.getId();
		List<ActivityDailyTypeCfg> cfgListIsOpen = new ArrayList<ActivityDailyTypeCfg>();
		List<ActivityDailyTypeCfg> cfgList = getAllCfg();
		for (ActivityDailyTypeCfg cfg : cfgList) {
			if (!StringUtils.equals(cfg.getId(), id) && isOpen(cfg)) {
				cfgListIsOpen.add(cfg);
			}
		}
		if (cfgListIsOpen.size() > 1) {
			GameLog.error(LogModule.ComActivityDailyCount, null,
					"更换版本号时发现同时有多个开启", null);
			return null;
		} else if (cfgListIsOpen.size() == 1) {
			return cfgListIsOpen.get(0);
		}
		return null;
	}

	public boolean isOpen(ActivityDailyTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
}