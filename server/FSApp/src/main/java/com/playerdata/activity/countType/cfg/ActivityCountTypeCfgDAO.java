package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityCountTypeCfgDAO extends
		CfgCsvDao<ActivityCountTypeCfg> {

	public static ActivityCountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityCountTypeCfgDAO.class);
	}

	private HashMap<String, List<ActivityCountTypeCfg>> enumIdCfgMapping;

	@Override
	public Map<String, ActivityCountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper
				.readCsv2Map("Activity/ActivityCountTypeCfg.csv",
						ActivityCountTypeCfg.class);
		for (ActivityCountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		HashMap<String, List<ActivityCountTypeCfg>> enumIdCfgMapping_ = new HashMap<String, List<ActivityCountTypeCfg>>();
		for (ActivityCountTypeCfg typeCfg : cfgCacheMap.values()) {
			ActivityTypeHelper.add(typeCfg,typeCfg.getEnumId(), enumIdCfgMapping_);
		}
		this.enumIdCfgMapping = enumIdCfgMapping_;
		return cfgCacheMap;
	}	

	public void parseTime(ActivityCountTypeCfg cfgItem) {
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem
				.getStartTimeStr());
		cfgItem.setStartTime(startTime);

		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem
				.getEndTimeStr());
		cfgItem.setEndTime(endTime);
	}

	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum
	 *            每日重置类型的活动,第几天
	 * @return
	 */
	public ActivityCountTypeItem newItem(Player player,
			ActivityCountTypeEnum countTypeEnum,
			ActivityCountTypeCfg activityCountTypeCfg) {
		if (activityCountTypeCfg != null) {
			ActivityCountTypeItem item = new ActivityCountTypeItem();
			String itemId = ActivityCountTypeHelper.getItemId(
					player.getUserId(), countTypeEnum);
			item.setId(itemId);
			item.setCfgId(activityCountTypeCfg.getId());
			item.setEnumId(activityCountTypeCfg.getEnumId());
			item.setUserId(player.getUserId());
			item.setVersion(activityCountTypeCfg.getVersion());
			item.setSubItemList(newItemList(player, activityCountTypeCfg));
			return item;
		} else {
			return null;
		}
	}

	public List<ActivityCountTypeSubItem> newItemList(Player player,
			ActivityCountTypeCfg activityCountTypeCfg) {
		List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
		List<ActivityCountTypeSubCfg> subItemCfgList = ActivityCountTypeSubCfgDAO
				.getInstance().getByParentCfgId(activityCountTypeCfg.getId());
		for (ActivityCountTypeSubCfg activityCountTypeSubCfg : subItemCfgList) {
			ActivityCountTypeSubItem subItem = new ActivityCountTypeSubItem();
			subItem.setCfgId(activityCountTypeSubCfg.getId());
			subItem.setCount(activityCountTypeSubCfg.getAwardCount());
			subItemList.add(subItem);
		}
		return subItemList;
	}

	/**
	 * 获取和传入数据同类型的，不同id的，处于激活状态的，单一新活动
	 */
	public ActivityCountTypeCfg getCfgByEnumId(ActivityCountTypeItem item) {
		String cfgId = item.getCfgId();
		String cfgEnumId = item.getEnumId();
		List<ActivityCountTypeCfg> cfgList = enumIdCfgMapping.get(item
				.getEnumId());
		List<ActivityCountTypeCfg> cfgListByEnum = new ArrayList<ActivityCountTypeCfg>();
		for (ActivityCountTypeCfg cfg : cfgList) {// 取出所有符合相同枚举的可选配置
			if (!StringUtils.equals(cfgId, cfg.getId())) {
				cfgListByEnum.add(cfg);
			}
		}
		List<ActivityCountTypeCfg> cfgListIsOpen = new ArrayList<ActivityCountTypeCfg>();// 激活的下一个活动，只有0或1个；
		for (ActivityCountTypeCfg cfg : cfgListByEnum) {
			if (isOpen(cfg)) {
				cfgListIsOpen.add(cfg);
			}
		}
		if (cfgListIsOpen.size() > 1) {
			GameLog.error(LogModule.ComActivityCount, null,
					"发现了两个以上开放的活动,活动枚举为=" + cfgEnumId, null);
			return null;
		} else if (cfgListIsOpen.size() == 1) {
			return cfgListIsOpen.get(0);
		}
		return null;
	}

	public boolean hasCfgListByEnumId(String enumId) {
		List<ActivityCountTypeCfg> typeCfgList = enumIdCfgMapping.get(enumId);
		if (typeCfgList == null || typeCfgList.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 检查指定enumId活动是否有已开放和满足玩家等级，如果有，返回true
	 * 
	 * @param playerLevel
	 * @param enumId
	 * @return
	 */
	public boolean isOpenAndLevelEnough(int playerLevel,
			ActivityCountTypeEnum enumId) {
		List<ActivityCountTypeCfg> typeCfgList = enumIdCfgMapping.get(enumId
				.getCfgId());
		if (typeCfgList == null) {
			return false;
		}
		for (int i = 0, size = typeCfgList.size(); i < size; i++) {
			ActivityCountTypeCfg cfg = typeCfgList.get(i);
			if (playerLevel >= cfg.getLevelLimit() && isOpen(cfg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查指定enumId活动是否有已开放，如果有，返回true
	 * 
	 * @param enumId
	 * @return
	 */
	public boolean isOpen(ActivityCountTypeEnum enumId) {
		List<ActivityCountTypeCfg> typeCfgList = enumIdCfgMapping.get(enumId
				.getCfgId());
		if (typeCfgList == null) {
			return false;
		}
		for (int i = 0, size = typeCfgList.size(); i < size; i++) {
			ActivityCountTypeCfg cfg = typeCfgList.get(i);
			if (isOpen(cfg)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOpen(ActivityCountTypeCfg activityCountTypeCfg, long currentTime) {
		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	public boolean isOpen(ActivityCountTypeCfg activityCountTypeCfg) {
		return isOpen(activityCountTypeCfg, System.currentTimeMillis());
	}
}