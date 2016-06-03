package com.playerdata.activity.dailyCountType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeSubItem;
import com.rw.fsutil.util.DateUtils;

public class ActivityDailyCountTypeMgr {

	private static ActivityDailyCountTypeMgr instance = new ActivityDailyCountTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityDailyCountTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityDailyCountTypeItemHolder.getInstance().synAllData(player);
	}



	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);	
		checkOtherDay(player);
		checkClose(player);

	}

	private void checkOtherDay(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		List<ActivityDailyCountTypeItem> item = dataHolder.getItemList(player.getUserId());
		ActivityDailyCountTypeCfg targetCfg = ActivityDailyCountTypeCfgDAO.getInstance().getConfig(ActivityDailyCountTypeEnum.Daily.getCfgId());
		if(targetCfg == null){
			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
			return;
		}
		for (ActivityDailyCountTypeItem targetItem : item) {
			if(DateUtils.getDayDistance(targetItem.getLastTime(), System.currentTimeMillis())>0){
				sendEmailIfGiftNotTaken(player, targetItem.getSubItemList() );
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}
	}
	private void checkClose(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		List<ActivityDailyCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityDailyCountTypeItem activityDailyCountTypeItem : itemList) {// 每种活动
			if (isClose(activityDailyCountTypeItem)) {
				sendEmailIfGiftNotTaken(player,  activityDailyCountTypeItem.getSubItemList());
				activityDailyCountTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityDailyCountTypeItem);
			}
		}
	}
	
	private boolean isClose(ActivityDailyCountTypeItem activityDailyCountTypeItem) {
	if (activityDailyCountTypeItem != null) {
		ActivityDailyCountTypeCfg cfgById = ActivityDailyCountTypeCfgDAO.getInstance().getCfgById(ActivityDailyCountTypeEnum.Daily.getCfgId());
		if(cfgById!=null){
			long endTime = cfgById.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime > endTime;
		}else{
			GameLog.error("activitydailycounttypemgr","" , "配置文件找不到数据奎对应的活动"+ ActivityDailyCountTypeEnum.Daily);
		}
	}
	return false;
}
	
	
	private void sendEmailIfGiftNotTaken(Player player,
			List<ActivityDailyCountTypeSubItem> subItemList) {
		for (ActivityDailyCountTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityDailyCountTypeSubCfg subItemCfg = ActivityDailyCountTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());
			if(subItemCfg == null){
				GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount()&&!subItem.isTaken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",subItemCfg.getId());
				subItem.setTaken(true);
				if (!isAdd) 
					GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
				}
		}		
	}

	private void checkCfgVersion(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		List<ActivityDailyCountTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityDailyCountTypeItem targetItem : itemList) {			
			ActivityDailyCountTypeCfg targetCfg = ActivityDailyCountTypeCfgDAO.getInstance().getConfig(ActivityDailyCountTypeEnum.Daily.getCfgId());
			if(targetCfg == null){
				GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
				return;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}
		
		
	}
	private void checkNewOpen(Player player) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		ActivityDailyCountTypeCfg activityCountTypeCfg = getparentCfg();
		if(activityCountTypeCfg == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
			return;
		}
		
		if(!isOpen(activityCountTypeCfg)){
			//活动未开启
			return ;
		}
		
		ActivityDailyCountTypeItem targetItem = dataHolder.getItem(player.getUserId());
		if(targetItem == null){
			targetItem = ActivityDailyCountTypeCfgDAO.getInstance().newItem(player);
			dataHolder.addItem(player, targetItem);
		}
	}
	
	public ActivityDailyCountTypeCfg getparentCfg(){
		List<ActivityDailyCountTypeCfg> allCfgList = ActivityDailyCountTypeCfgDAO.getInstance().getAllCfg();		
		if(allCfgList == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "不存在每日活动" );
			return null;			
		}		
		if(allCfgList.size() != 1){
			GameLog.error("activityDailyCountTypeMgr", "list", "同时存在多个每日活动" + allCfgList.size());
			return null;
		}		
		ActivityDailyCountTypeCfg activityCountTypeCfg = allCfgList.get(0);		
		return activityCountTypeCfg;
	}
	







	private boolean isOpen(ActivityDailyCountTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}
	
	public boolean isLevelEnough(Player player) {
		ActivityDailyCountTypeCfg activityCountTypeCfg = getparentCfg();
		if(activityCountTypeCfg == null){
			GameLog.error("activityDailyCountTypeMgr", "list", "配置文件总表错误" );
			return false;
		}
		if(player.getLevel() < activityCountTypeCfg.getLevelLimit()){
			return false;
		}		
		return true;
	}
	
	public boolean isOpen(ActivityDailyCountTypeSubCfg activityCountTypesubCfg) {
		
		
		if (activityCountTypesubCfg != null) {
			
			long startTime = activityCountTypesubCfg.getStartTime();
			long endTime = activityCountTypesubCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}
	
	public void addCount(Player player, ActivityDailyCountTypeEnum countType, int countadd) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();
		ActivityDailyCountTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityDailyCountTypeSubItem subItem = getbyDailyCountTypeEnum(player, countType, dataItem);	
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}
	
	public ActivityDailyCountTypeSubItem getbyDailyCountTypeEnum (Player player,ActivityDailyCountTypeEnum typeEnum,ActivityDailyCountTypeItem dataItem){		
		ActivityDailyCountTypeSubItem subItem = null;
		ActivityDailyCountTypeSubCfg cfg = null;
		List<ActivityDailyCountTypeSubCfg> subcfglist = ActivityDailyCountTypeSubCfgDAO.getInstance().getAllCfg();
		for(ActivityDailyCountTypeSubCfg subcfg :subcfglist){
			if(StringUtils.equals(subcfg.getId(), typeEnum.getCfgId())){
			cfg = subcfg;
			break;
			}
		}
		if(cfg == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启中,但活动配置生成的cfg没有对应的事件枚举");
			return subItem;
		}
		
		if(dataItem != null){
			List<ActivityDailyCountTypeSubItem> sublist = dataItem.getSubItemList();
			for(ActivityDailyCountTypeSubItem subitem : sublist){
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
	
	
	
	public ActivityComResult takeGift(Player player, ActivityDailyCountTypeEnum countType, String subItemId) {
		ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();

		ActivityDailyCountTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityDailyCountTypeSubItem targetItem = null;

			List<ActivityDailyCountTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityDailyCountTypeSubItem itemTmp : subItemList) {
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

	private void takeGift(Player player, ActivityDailyCountTypeSubItem targetItem) {
		ActivityDailyCountTypeSubCfg subCfg = ActivityDailyCountTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(subCfg == null){
			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
			return;
		}
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());

	}

}
