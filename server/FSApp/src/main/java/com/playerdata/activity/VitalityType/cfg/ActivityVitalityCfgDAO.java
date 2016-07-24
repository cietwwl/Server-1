package com.playerdata.activity.VitalityType.cfg;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
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
			int day = getday();
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
	
	

	/**根据当前时间返回处于活动之王活动的第几天,相对间隔天数，以hour点为基准*/
	public int getday() {
		ActivityVitalityCfg cfgById = getConfig(ActivityVitalityTypeEnum.Vitality.getCfgId());
		if(cfgById == null){
			return 0;
		}
		long startTime = cfgById.getStartTime();
		long currentTime = System.currentTimeMillis();
//		int day = DateUtils.getDayDistance(startTime, currentTime);
		int day = DateUtils.getDayLimitHour(5, startTime); 
		day++;		
		return day;
	}

	public List<ActivityVitalityTypeSubItem> newItemList(int day,ActivityVitalityTypeEnum eNum) {
		List<ActivityVitalityTypeSubItem> subItemList = null;
		List<ActivityVitalitySubCfg> allsubCfgList = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();
		if(eNum == ActivityVitalityTypeEnum.Vitality){
			subItemList = newItemListOne(day,eNum,allsubCfgList);	
		}			
		if(eNum == ActivityVitalityTypeEnum.VitalityTwo){
			subItemList = newItemListTwo(day,eNum,allsubCfgList);
		}
		return subItemList;
	}

	private List<ActivityVitalityTypeSubItem> newItemListOne(int day,
			ActivityVitalityTypeEnum eNum,
			List<ActivityVitalitySubCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
		for(ActivityVitalitySubCfg activityVitalitySubCfg : allsubCfgList){
			if(activityVitalitySubCfg.getDay() != day){
				continue;
			}
			
			ActivityVitalityTypeSubItem subitem = new ActivityVitalityTypeSubItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subitem.setType(activityVitalitySubCfg.getType());
			subItemList.add(subitem);
		}	
		return subItemList;
	}
	
	private List<ActivityVitalityTypeSubItem> newItemListTwo(int day,
			ActivityVitalityTypeEnum eNum,
			List<ActivityVitalitySubCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubItem> subItemList = new ArrayList<ActivityVitalityTypeSubItem>();
		for(ActivityVitalitySubCfg activityVitalitySubCfg : allsubCfgList){
			String eNumStr = eNum.getCfgId();
			if(!StringUtils.equals(eNumStr, activityVitalitySubCfg.getActiveType()+"")){
				
				continue;
			}			
			ActivityVitalityTypeSubItem subitem = new ActivityVitalityTypeSubItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(0);
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subitem.setType(activityVitalitySubCfg.getType());
			subItemList.add(subitem);
		}	
		return subItemList;
	}
	

	public List<ActivityVitalityTypeSubBoxItem> newBoxItemList(int day,ActivityVitalityTypeEnum eNum) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = null;
		List<ActivityVitalityRewardCfg> allsubCfgList = ActivityVitalityRewardCfgDAO.getInstance().getAllCfg();
		if(eNum == ActivityVitalityTypeEnum.Vitality){
			subItemList = newBoxItemListOne(day,eNum,allsubCfgList);	
		}			
		if(eNum == ActivityVitalityTypeEnum.VitalityTwo){
			subItemList = newBoxItemListTwo(day,eNum,allsubCfgList);
		}
		return subItemList;
		
		
		
//		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
//		List< ActivityVitalityRewardCfg> allsubCfgList = ActivityVitalityRewardCfgDAO.getInstance().getAllCfg();	
//		for(ActivityVitalityRewardCfg activityVitalitySubCfg : allsubCfgList){
//			if(activityVitalitySubCfg.getDay() != day){
//				continue;
//			}
//			
//			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
//			subitem.setCfgId(activityVitalitySubCfg.getId());
//			subitem.setCount(activityVitalitySubCfg.getActivecount());
//			subitem.setTaken(false);
//			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
//			subItemList.add(subitem);
//		}		
//		return subItemList;
	}
	
	private List<ActivityVitalityTypeSubBoxItem> newBoxItemListTwo(int day,
			ActivityVitalityTypeEnum eNum,
			List<ActivityVitalityRewardCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
		boolean isempty=true;
		if(allsubCfgList == null){
			subItemList = null;
			return subItemList;
		}
		for(ActivityVitalityRewardCfg rewardCfg : allsubCfgList){
			if(rewardCfg.getActiveType()==Integer.parseInt(eNum.getCfgId())){
				isempty = false;
				break;
			}
		}
		
		if(isempty){			
			subItemList = null;
			return subItemList;
		}
		for(ActivityVitalityRewardCfg activityVitalitySubCfg : allsubCfgList){
			String eNumStr = eNum.getCfgId();
			if(!StringUtils.equals(eNumStr, activityVitalitySubCfg.getActiveType()+"")){
				
				continue;
			}			
			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
			subitem.setCfgId(activityVitalitySubCfg.getId());
			subitem.setCount(activityVitalitySubCfg.getActivecount());
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalitySubCfg.getGiftId());
			subItemList.add(subitem);
		}	
		return subItemList;
	}

	private List<ActivityVitalityTypeSubBoxItem> newBoxItemListOne(int day,
			ActivityVitalityTypeEnum eNum,
			List<ActivityVitalityRewardCfg> allsubCfgList) {
		List<ActivityVitalityTypeSubBoxItem> subItemList = new ArrayList<ActivityVitalityTypeSubBoxItem>();
		boolean isempty=true;
		if(allsubCfgList == null){
			subItemList = null;
			return subItemList;
		}
		for(ActivityVitalityRewardCfg rewardCfg : allsubCfgList){
			if(rewardCfg.getActiveType()==Integer.parseInt(eNum.getCfgId())){
				isempty = false;
				break;
			}
		}
		
		if(isempty){			
			subItemList = null;
			return subItemList;
		}
		for(ActivityVitalityRewardCfg activityVitalityRewardCfg : allsubCfgList){
			if(activityVitalityRewardCfg.getDay() != day){
				continue;
			}
			
			ActivityVitalityTypeSubBoxItem subitem = new ActivityVitalityTypeSubBoxItem();
			subitem.setCfgId(activityVitalityRewardCfg.getId());
			subitem.setCount(activityVitalityRewardCfg.getActivecount());
			subitem.setTaken(false);
			subitem.setGiftId(activityVitalityRewardCfg.getGiftId());
			subItemList.add(subitem);
		}
		return subItemList;
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
	
	public ActivityVitalityCfg getparentCfg(){
		List<ActivityVitalityCfg> allCfgList = getAllCfg();		
		if(allCfgList == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "不存在每日活动" );
			return null;			
		}		
		
		ActivityVitalityCfg vitalityCfg = allCfgList.get(0);		
		return vitalityCfg;
	}

}