package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
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
public final class ActivityCountTypeCfgDAO extends CfgCsvDao<ActivityCountTypeCfg> {


	


	public static ActivityCountTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityCountTypeCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityCountTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityCountTypeCfg.csv", ActivityCountTypeCfg.class);
		for (ActivityCountTypeCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	



	public void parseTime(ActivityCountTypeCfg cfgItem){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getStartTimeStr());
		cfgItem.setStartTime(startTime);
		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfgItem.getEndTimeStr());
		cfgItem.setEndTime(endTime);		
	}
		
	

	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  每日重置类型的活动,第几天
	 * @return
	 */
	public ActivityCountTypeItem newItem(Player player, ActivityCountTypeEnum countTypeEnum,ActivityCountTypeCfg activityCountTypeCfg){
		
		
		if(activityCountTypeCfg!=null){			
			String cfgId = activityCountTypeCfg.getId();
			ActivityCountTypeItem item = new ActivityCountTypeItem();
			String itemId = ActivityCountTypeHelper.getItemId(player.getUserId(), countTypeEnum);
			item.setId(itemId);
			item.setCfgId(cfgId);
			item.setEnumId(activityCountTypeCfg.getEnumId());
			item.setUserId(player.getUserId());
			item.setVersion(activityCountTypeCfg.getVersion());
			item.setSubItemList(newItemList(player, activityCountTypeCfg));
			return item;
		}else{
			return null;
		}		
		
	}
	
	
	public List<ActivityCountTypeSubItem> newItemList(Player player, ActivityCountTypeCfg activityCountTypeCfg) {
		List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
		List<ActivityCountTypeSubCfg> subItemCfgList = ActivityCountTypeSubCfgDAO.getInstance().getByParentCfgId(activityCountTypeCfg.getId());
		for (ActivityCountTypeSubCfg activityCountTypeSubCfg : subItemCfgList) {
			ActivityCountTypeSubItem subItem = new ActivityCountTypeSubItem();
			subItem.setCfgId(activityCountTypeSubCfg.getId());	
			subItem.setCount(activityCountTypeSubCfg.getAwardCount());
			subItemList.add(subItem);
		}	
		return subItemList;
	}

	/**
	 *获取和传入数据同类型的，不同id的，处于激活状态的，单一新活动 
	 */
	public ActivityCountTypeCfg getCfgByEnumId(ActivityCountTypeItem item) {
		String cfgId = item.getCfgId();
		String cfgEnumId = item.getEnumId();
		List<ActivityCountTypeCfg>  cfgList = getAllCfg();
		List<ActivityCountTypeCfg>  cfgListByEnum = new ArrayList<ActivityCountTypeCfg>();
		for(ActivityCountTypeCfg cfg : cfgList){//取出所有符合相同枚举的可选配置
			if(StringUtils.equals(cfgEnumId, cfg.getEnumId())&&!StringUtils.equals(cfgId, cfg.getId())){
				cfgListByEnum.add(cfg);
			}			
		}
		
		List<ActivityCountTypeCfg>  cfgListIsOpen = new ArrayList<ActivityCountTypeCfg>();//激活的下一个活动，只有0或1个；
		for(ActivityCountTypeCfg cfg : cfgListByEnum){
			if(ActivityCountTypeMgr.getInstance().isOpen(cfg)){
				cfgListIsOpen.add(cfg);
			}			
		}
		
		if(cfgListIsOpen.size() > 1){
			GameLog.error(LogModule.ComActivityCount, null, "发现了两个以上开放的活动,活动枚举为="+ cfgEnumId, null);
			return null;
		}else if(cfgListIsOpen.size() == 1){
			return cfgListIsOpen.get(0);
		}		
		return null;
	}

	


}