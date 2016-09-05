package com.playerdata.activity.dailyCountType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class ActivityDailyTypeSubCfgDAO extends
		CfgCsvDao<ActivityDailyTypeSubCfg> {
	public static ActivityDailyTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDailyTypeSubCfgDAO.class);
	}

	private HashMap<String, List<ActivityDailyTypeSubCfg>> cfgMapByParentid;
	private HashMap<String, List<ActivityDailyTypeSubCfg>> cfgMapByEnumid;

	@Override
	public Map<String, ActivityDailyTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityDailyCountTypeSubCfg.csv",
				ActivityDailyTypeSubCfg.class);
		for (ActivityDailyTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		HashMap<String, List<ActivityDailyTypeSubCfg>> mapParentidTmp = new HashMap<String, List<ActivityDailyTypeSubCfg>>();
		HashMap<String, List<ActivityDailyTypeSubCfg>> mapEnumidTmp = new HashMap<String, List<ActivityDailyTypeSubCfg>>();
		for (ActivityDailyTypeSubCfg subCfg : cfgCacheMap.values()) {
			String parentid = subCfg.getParentId();
			String enumid = subCfg.getEnumId();
			List<ActivityDailyTypeSubCfg> list = mapParentidTmp.get(parentid);
			List<ActivityDailyTypeSubCfg> listByEnumid = mapEnumidTmp
					.get(enumid);
			if (list == null) {
				list = new ArrayList<ActivityDailyTypeSubCfg>();
				mapParentidTmp.put(parentid, list);
			}
			if (listByEnumid == null) {
				listByEnumid = new ArrayList<ActivityDailyTypeSubCfg>();
				mapEnumidTmp.put(enumid, listByEnumid);
			}
			listByEnumid.add(subCfg);
			list.add(subCfg);
		}
		this.cfgMapByParentid = mapParentidTmp;
		this.cfgMapByEnumid = mapEnumidTmp;
		return cfgCacheMap;
	}

	private void parseTime(ActivityDailyTypeSubCfg cfgTmp) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgTmp
				.getStartTimeStr());
		cfgTmp.setStartTime(startTime);

		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgTmp
				.getEndTimeStr());
		cfgTmp.setEndTime(endTime);
	}

	/**
	 * 根据id查找subCfg
	 * */
	public ActivityDailyTypeSubCfg getById(String subId) {
		return cfgCacheMap.get(subId);
	}	

	public List<ActivityDailyTypeSubCfg> getCfgMapByEnumid(String enumid) {
		return cfgMapByEnumid.get(enumid);
	}

	public List<ActivityDailyTypeSubCfg> getCfgMapByParentid(String parentid) {
		List<ActivityDailyTypeSubCfg> list = cfgMapByParentid.get(parentid);
		if(list == null){
			list = new ArrayList<ActivityDailyTypeSubCfg>();
		}
		return list;
	}

	/** 用于验证单个cfg是否开启 */
	public boolean isOpen(ActivityDailyTypeSubCfg activityCountTypesubCfg) {
		if (activityCountTypesubCfg != null) {
			long startTime = activityCountTypesubCfg.getStartTime();
			long endTime = activityCountTypesubCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}

	/** 用于验证某个功能对应的多个subCfg是否存在开启的并且等级足以触发 */
	public boolean isOpenAndLevelEnough(int playerlevel,
			List<ActivityDailyTypeSubCfg> subList) {
		if(subList == null || subList.isEmpty()){
			//策划把子表删除了
			return false;
		}
		ActivityDailyTypeCfgDAO activityDailyTypeCfgDAO = ActivityDailyTypeCfgDAO.getInstance();
		ActivityDailyTypeSubCfg tmp = null;
		boolean isOpen = false;
		for (ActivityDailyTypeSubCfg subCfg : subList) {
			if (isOpen(subCfg)) {
				isOpen = true;
				tmp = subCfg;// 子活动开启
				break;
			}
		}
		if (tmp == null) {
			return isOpen;
		}
		ActivityDailyTypeCfg cfg = activityDailyTypeCfgDAO.getCfgById(tmp.getParentId());
		if (cfg == null) {
			return isOpen;
		}
		return playerlevel >= cfg.getLevelLimit()
				&& activityDailyTypeCfgDAO.isOpen(cfg);// 活动开启；活动等级足够
	}

	/** 用于验证某个功能对应的多个subCfg是否存在开启的 */
	public boolean isOpen(List<ActivityDailyTypeSubCfg> subList) {
		ActivityDailyTypeSubCfg tmp = null;
		boolean isOpen = false;
		ActivityDailyTypeCfgDAO activityDailyTypeCfgDAO = ActivityDailyTypeCfgDAO.getInstance();
		for (ActivityDailyTypeSubCfg subCfg : subList) {
			if (isOpen(subCfg)) {
				isOpen = true;
				tmp = subCfg;// 子活动开启
				break;
			}
		}
		if (tmp == null) {
			return isOpen;
		}
		ActivityDailyTypeCfg cfg = ActivityDailyTypeCfgDAO.getInstance()
				.getCfgById(tmp.getParentId());
		if (cfg == null) {
			return isOpen;
		}
		return activityDailyTypeCfgDAO.isOpen(cfg);// 活动开启；活动等级足够
	}
}