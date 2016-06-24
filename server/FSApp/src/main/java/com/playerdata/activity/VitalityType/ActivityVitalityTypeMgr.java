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
import com.rw.fsutil.util.DateUtils;


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
		checkCfgVersion(player);
		checkOtherDay(player);
		checkClose(player);
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
	
	private void checkCfgVersion(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
	List<ActivityVitalityTypeItem> itemList = dataHolder.getItemList(player.getUserId());
	ActivityVitalityCfg targetCfg = ActivityVitalityCfgDAO.getInstance().getparentCfg();
	if(targetCfg == null){
		GameLog.error(LogModule.ComActivityVitality, null, "通用活动找不到配置文件", null);
		return;
	}	
	for (ActivityVitalityTypeItem targetItem : itemList) {			
		if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
			targetItem.reset(targetCfg);
			dataHolder.updateItem(player, targetItem);
		}
	}		
}

	private void checkOtherDay(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> item = dataHolder.getItemList(player.getUserId());
		ActivityVitalityCfg targetCfg = ActivityVitalityCfgDAO.getInstance().getparentCfg();
		if(targetCfg == null){
			GameLog.error(LogModule.ComActivityVitality, null, "通用活动找不到配置文件", null);
			return;
		}	
		for (ActivityVitalityTypeItem targetItem : item) {
			if(DateUtils.getDayDistance(targetItem.getLastTime(), System.currentTimeMillis())>0){
				sendEmailIfGiftNotTaken(player, targetItem.getSubItemList());//补发过期奖励
				sendEmailIfBoxGiftNotTaken(player, targetItem);//补发过期宝箱奖励，区分是否宝箱刷新
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}
	}
	

	
	
	private void checkClose(Player player) {
		ActivityVitalityItemHolder dataHolder = ActivityVitalityItemHolder.getInstance();
		List<ActivityVitalityTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityVitalityTypeItem activityVitalityTypeItem : itemList) {// 每种活动
			if (isClose(activityVitalityTypeItem)) {
				sendEmailIfGiftNotTaken(player,  activityVitalityTypeItem.getSubItemList());
				sendEmailIfBoxGiftNotTaken(player, activityVitalityTypeItem);
				activityVitalityTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityVitalityTypeItem);
			}
		}
	}
	
	
	

	

	public boolean isOpen( ) {
		ActivityVitalityCfg vitalityCfg = ActivityVitalityCfgDAO.getInstance().getparentCfg();
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
	
	
	private void sendEmailIfGiftNotTaken(Player player,List<ActivityVitalityTypeSubItem> subItemList) {
		for (ActivityVitalityTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityVitalitySubCfg subItemCfg = ActivityVitalitySubCfgDAO.getInstance().getById(subItem.getCfgId());
			if (subItemCfg == null) {
				GameLog.error(LogModule.ComActivityVitality, null,
						"通用活动找不到配置文件", null);
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount()
					&& !subItem.isTaken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
						player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",
						subItemCfg.getId());
				subItem.setTaken(true);
				if (!isAdd)
					GameLog.error(LogModule.ComActivityVitality,
							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
			}
		}
	}
	
	private void sendEmailIfBoxGiftNotTaken(Player player,ActivityVitalityTypeItem Item) {
		if(ActivityVitalityCfgDAO.getInstance().getparentCfg().getIsCanGetReward() == 1){
			//宝箱功能不开放 
			return;
		}
		
		List<ActivityVitalityTypeSubBoxItem> subBoxItemList = Item.getSubBoxItemList();
		
		for (ActivityVitalityTypeSubBoxItem subItem : subBoxItemList) {// 配置表里的每种奖励
			ActivityVitalityRewardCfg subItemCfg = ActivityVitalityRewardCfgDAO.getInstance().getById(subItem.getCfgId());
			if (subItemCfg == null) {
				GameLog.error(LogModule.ComActivityVitality, null,
						"通用活动找不到配置文件", null);
				return;
			}
			if (Item.getActiveCount() >= subItemCfg.getActivecount()
					&& !subItem.isTaken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(
						player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",
						subItemCfg.getId());
				subItem.setTaken(true);
				if (!isAdd)
					GameLog.error(LogModule.ComActivityVitality,
							player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
			}
		}
		
	}
	
	
	
	private boolean isClose(ActivityVitalityTypeItem activityVitalityTypeItem) {
	if (activityVitalityTypeItem != null) {
		ActivityVitalityCfg cfgById = ActivityVitalityCfgDAO.getInstance().getparentCfg();
		if(cfgById!=null){
			long endTime = cfgById.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime > endTime;
		}else{
			GameLog.error("activitydailycounttypemgr","" , "配置文件找不到数据奎对应的活动");
		}
	}
	return false;
}
	
//	
	
	public boolean isLevelEnough(Player player) {
		ActivityVitalityCfg vitalityCfg = ActivityVitalityCfgDAO.getInstance().getparentCfg();
		if(vitalityCfg == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
			return false;
		}
		if(player.getLevel() < vitalityCfg.getLevelLimit()){
			return false;
		}		
		return true;
	}
	
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
			if(ActivityVitalityCfgDAO.getInstance().getday() != subcfg.getDay()){
				continue;
			}			
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




	
	

}
