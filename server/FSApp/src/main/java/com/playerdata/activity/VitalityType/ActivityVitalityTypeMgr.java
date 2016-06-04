package com.playerdata.activity.VitalityType;


import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityRewardCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItemHolder;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubBoxItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;


public class ActivityVitalityTypeMgr {

	private static ActivityVitalityTypeMgr instance = new ActivityVitalityTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityVitalityTypeMgr getInstance() {
		return instance;
	}

	public void synVitalityTypeData(Player player) {
		ActivityVitalityItemHolder.getInstance().synAllData(player);
	}



	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
//		checkCfgVersion(player);	
//		checkOtherDay(player);
//		checkClose(player);
	}
	
	private void checkNewOpen(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder
				.getInstance();
		
		

		if (!isOpen()) {
			// 活动未开启
			return;
		}

		ActivityVitalityTypeItem targetItem = dataHolder.getItem(player
				.getUserId());
		if (targetItem == null) {
			targetItem = ActivityVitalityCfgDAO.getInstance().newItem(player);
			dataHolder.addItem(player, targetItem);
		}
	}
	
//	private void checkCfgVersion(Player player) {
//	ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
//	List<ActivityDailyCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
//	for (ActivityDailyCountTypeItem targetItem : itemList) {			
//		ActivityDailyCountTypeCfg targetCfg = ActivityDailyCountTypeCfgDAO.getInstance().getConfig(ActivityDailyCountTypeEnum.Daily.getCfgId());
//		if(targetCfg == null){
//			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
//			return;
//		}			
//		
//		if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
//			targetItem.reset(targetCfg);
//			dataHolder.updateItem(player, targetItem);
//		}
//	}		
//}
//
//	private void checkOtherDay(Player player) {
//		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
//		List<ActivityDailyCountTypeItem> item = dataHolder.getItemList(player.getUserId());
//		ActivityDailyCountTypeCfg targetCfg = ActivityDailyCountTypeCfgDAO.getInstance().getConfig(ActivityDailyCountTypeEnum.Daily.getCfgId());
//		if(targetCfg == null){
//			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
//			return;
//		}
//		for (ActivityDailyCountTypeItem targetItem : item) {
//			if(DateUtils.getDayDistance(targetItem.getLastTime(), System.currentTimeMillis())>0){
//				sendEmailIfGiftNotTaken(player, targetItem.getSubItemList() );
//				targetItem.reset(targetCfg);
//				dataHolder.updateItem(player, targetItem);
//			}
//		}
//	}
//	private void checkClose(Player player) {
//		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
//		List<ActivityDailyCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
//
//		for (ActivityDailyCountTypeItem activityDailyCountTypeItem : itemList) {// 每种活动
//			if (isClose(activityDailyCountTypeItem)) {
//				sendEmailIfGiftNotTaken(player,  activityDailyCountTypeItem.getSubItemList());
//				activityDailyCountTypeItem.setClosed(true);
//				dataHolder.updateItem(player, activityDailyCountTypeItem);
//			}
//		}
//	}
	
	public ActivityVitalityCfg getparentCfg(){
		List<ActivityVitalityCfg> allCfgList = ActivityVitalityCfgDAO.getInstance().getAllCfg();		
		if(allCfgList == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "不存在每日活动" );
			return null;			
		}		
		if(allCfgList.size() != 1){
			GameLog.error("activityDailyCountTypeMgr", "list", "同时存在多个每日活动" + allCfgList.size());
			return null;
		}		
		ActivityVitalityCfg vitalityCfg = allCfgList.get(0);		
		return vitalityCfg;
	}
	

	public boolean isOpen( ) {
		ActivityVitalityCfg vitalityCfg = getparentCfg();
		if (vitalityCfg == null) {
			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误");
			return false;
		}
		long startTime = vitalityCfg.getStartTime();
		long endTime = vitalityCfg.getEndTime();
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime > startTime;
	}
//	
	public boolean isLevelEnough(Player player) {
		ActivityVitalityCfg vitalityCfg = getparentCfg();
		if(vitalityCfg == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
			return false;
		}
		if(player.getLevel() < vitalityCfg.getLevelLimit()){
			return false;
		}		
		return true;
	}
	
	
	
	
//	private boolean isClose(ActivityDailyCountTypeItem activityDailyCountTypeItem) {
//	if (activityDailyCountTypeItem != null) {
//		ActivityDailyCountTypeCfg cfgById = ActivityDailyCountTypeCfgDAO.getInstance().getCfgById(ActivityDailyCountTypeEnum.Daily.getCfgId());
//		if(cfgById!=null){
//			long endTime = cfgById.getEndTime();
//			long currentTime = System.currentTimeMillis();
//			return currentTime > endTime;
//		}else{
//			GameLog.error("activitydailycounttypemgr","" , "配置文件找不到数据奎对应的活动"+ ActivityDailyCountTypeEnum.Daily);
//		}
//	}
//	return false;
//}
	
//	
	public void addCount(Player player, ActivityVitalityTypeEnum countType,ActivityVitalitySubCfg subCfg, int countadd) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId());		
		ActivityVitalityTypeSubItem subItem = getbyVitalityTypeEnum(player, countType, dataItem);	
		
		addVitalitycount(dataItem,subItem,subCfg,countadd);
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}
	
	/**增加活跃度*/
    private void addVitalitycount(ActivityVitalityTypeItem dataItem, ActivityVitalityTypeSubItem subItem,
    		ActivityVitalitySubCfg subCfg,int countadd) {
		if(subItem.getCount() < subCfg.getCount() && (subItem.getCount() + countadd >= subCfg.getCount())){
			dataItem.setActiveCount(dataItem.getActiveCount() + subCfg.getActiveCount());
		}   	
	}

	//	
	public ActivityVitalityTypeSubItem getbyVitalityTypeEnum (Player player,ActivityVitalityTypeEnum typeEnum,ActivityVitalityTypeItem dataItem){		
		ActivityVitalityTypeSubItem subItem = null;
		ActivityVitalitySubCfg cfg = null;
		List<ActivityVitalitySubCfg> subcfglist = ActivityVitalitySubCfgDAO.getInstance().getAllCfg();
		for(ActivityVitalitySubCfg subcfg :subcfglist){
			if(StringUtils.equals(subcfg.getType(), typeEnum.getCfgId())){
			cfg = subcfg;
			break;
			}
		}
		if(cfg == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启中,但活动配置生成的cfg没有对应的事件枚举");
			return subItem;
		}
		
		if(dataItem != null){
			List<ActivityVitalityTypeSubItem> sublist = dataItem.getSubItemList();
			for(ActivityVitalityTypeSubItem subitem : sublist){
				if(StringUtils.equals(cfg.getId(), subitem.getCfgId())){				
					subItem = subitem;
					break;
				}
			}			
		}
		
		if(subItem == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启,找到了cfg,玩家数据每找到item或subitem");
		}		
		return   subItem;
	}
	
	
	public ActivityComResult takeGift(Player player, String subItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityVitalityTypeSubItem targetItem = null;
			List<ActivityVitalityTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityVitalityTypeSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			
			if (targetItem != null && !targetItem.isTaken()) {
				takeGift(player, targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}

		}

		return result;
	}
	
	private void takeGift(Player player, ActivityVitalityTypeSubItem targetItem) {		
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());
	}

	public ActivityComResult openBox(Player player, String rewardItemId) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		ActivityVitalityTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");
		} else {
			ActivityVitalityTypeSubBoxItem targetItem = null;
			List<ActivityVitalityTypeSubBoxItem> subItemList = dataItem.getSubBoxItemList();
			for (ActivityVitalityTypeSubBoxItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), rewardItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
		
			if (targetItem != null && !targetItem.isTaken()) {
				takeBoxGift(player, targetItem);
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}
		}
		return null;
	}

	private void takeBoxGift(Player player,	ActivityVitalityTypeSubBoxItem targetItem) {
		
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, targetItem.getGiftId());
		
	}



//	private void sendEmailIfGiftNotTaken(Player player,
//			List<ActivityDailyCountTypeSubItem> subItemList) {
//		for (ActivityDailyCountTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
//			ActivityDailyCountTypeSubCfg subItemCfg = ActivityDailyCountTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());
//			if(subItemCfg == null){
//				GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
//				return;
//			}
//			if (subItem.getCount() >= subItemCfg.getCount()&&!subItem.isTaken()) {
//				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",subItemCfg.getId());
//				subItem.setTaken(true);
//				if (!isAdd) 
//					GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
//				}
//		}		
//	}
	
	

}
