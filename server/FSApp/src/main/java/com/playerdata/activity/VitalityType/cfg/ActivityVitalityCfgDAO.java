package com.playerdata.activity.VitalityType.cfg;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeHelper;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityVitalityCfgDAO extends CfgCsvDao<ActivityVitalityCfg> {
	

	public static ActivityVitalityCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalityCfgDAO.class);
	}
	
	@Override
	public Map<String, ActivityVitalityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityTypeCfg.csv", ActivityVitalityCfg.class);
		for (ActivityVitalityCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		return cfgCacheMap;
	}


	public void parseTime(ActivityVitalityCfg cfg){
		long startTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getStartTimeStr());
		cfg.setStartTime(startTime);		
		long endTime = DateUtils.YyyymmddhhmmToMillionseconds(cfg.getEndTimeStr());
		cfg.setEndTime(endTime);		
	}		
	
	public ActivityVitalityCfg getConfig(String id){
		ActivityVitalityCfg cfg = getCfgById(id);
		return cfg;
	}
	
	/**
	 * 
	 * @param player
	 * @param countTypeEnum
	 * @param subdaysNum  无数据记录的玩家根据第几天开始参与活跃之王来生成数据
	 * @return
	 */
	public ActivityVitalityTypeItem newItem(Player player,ActivityVitalityTypeEnum aVitalityTypeEnum){
		ActivityVitalityCfg cfgById = getConfig(aVitalityTypeEnum.getCfgId());
		if(cfgById!=null){
			int day = ActivityVitalityCfgDAO.getInstance().getday() ;
			ActivityVitalityTypeItem item = new ActivityVitalityTypeItem();	
			String itemId = ActivityVitalityTypeHelper.getItemId(player.getUserId(), aVitalityTypeEnum);
			item.setId(itemId);
			item.setCfgId(cfgById.getId());
			item.setUserId(player.getUserId());
			item.setVersion(cfgById.getVersion());
			item.setActiveCount(0);
			item.setSubItemList(newItemList(day,aVitalityTypeEnum));
			List<ActivityVitalityTypeSubBoxItem> boxlist = newBoxItemList(day,aVitalityTypeEnum);
			if(boxlist != null){
				item.setSubBoxItemList(boxlist);
			}
			item.setLastTime(System.currentTimeMillis());
			item.setCanGetReward(cfgById.isCanGetReward());
			return item;
		}else{
			return null;
		}		
	}
	
	/**防止策划把活跃之王的配置表删除，导致报空*/
	public int getday(){
		ActivityVitalityCfg cfg = ActivityVitalityCfgDAO.getInstance().getConfig(ActivityVitalityTypeEnum.Vitality.getCfgId());
		if(cfg == null){
			return 0;
		}		
		return ActivityTypeHelper.getDayBy5Am(cfg.getStartTime());
	
	}

	

	public List<ActivityVitalityTypeSubItem> newItemList(int day,ActivityVitalityTypeEnum eNum) {
		 List<ActivityVitalityTypeSubItem>	subItemList = null;		
		List<ActivityVitalitySubCfg> allsubCfgList = ActivityVitalitySubCfgDAO.getInstance().getCfgListByEnum(eNum);
		for(ActivityVitalitySubCfg activityVitalitySubCfg : allsubCfgList){			
			if(eNum==ActivityVitalityTypeEnum.Vitality&&activityVitalitySubCfg.getDay() != day){
				continue;
			}			
			ActivityVitalityTypeSubItem subitem = new ActivityVitalityTypeSubItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subitem.setType(activityVitalitySubCfg.getType());
			if(subItemList == null){
				subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
			}
			subItemList.add(subitem);
		}			
		return subItemList;
	}

	
	
	public List<ActivityVitalityTypeSubBoxItem> newBoxItemList(int day,ActivityVitalityTypeEnum eNum) {
		List<ActivityVitalityTypeSubBoxItem> subBoxItemList = null;
		List<ActivityVitalityRewardCfg> allRewardCfgList = ActivityVitalityRewardCfgDAO.getInstance().getCfgListByEnum(eNum);
		for(ActivityVitalityRewardCfg activityVitalityRewardCfg : allRewardCfgList){
			if(eNum==ActivityVitalityTypeEnum.Vitality&&activityVitalityRewardCfg.getDay() != day){
				continue;
			}
			ActivityVitalityTypeSubBoxItem subBoxItem = new ActivityVitalityTypeSubBoxItem();
			subBoxItem.setCfgId(activityVitalityRewardCfg.getId());
			subBoxItem.setCount(activityVitalityRewardCfg.getActivecount());
			subBoxItem.setTaken(false);
			subBoxItem.setGiftId(activityVitalityRewardCfg.getGiftId());
			if(subBoxItemList == null){
				subBoxItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
			}
			subBoxItemList.add(subBoxItem);
		}
		return subBoxItemList;
	}
	
	
	
	
	public ActivityVitalityCfg getCfgByItem(ActivityVitalityTypeItem Item){
		List<ActivityVitalityCfg> cfglist = ActivityVitalityCfgDAO.getInstance().getAllCfg();
		ActivityVitalityCfg cfg = null;
		for(ActivityVitalityCfg activityVitalityCfg : cfglist){
			if(StringUtils.equals(Item.getCfgId(), activityVitalityCfg.getId())){
				cfg = activityVitalityCfg;
				break;
			}			
		}
		return cfg;
	}
	


}