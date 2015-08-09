package com.playerdata.activity.dailyCountType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.ActivityRedPointUpdate;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;

public class ActivityDailyTypeMgr implements ActivityRedPointUpdate{

	private static ActivityDailyTypeMgr instance = new ActivityDailyTypeMgr();

	private final static int MAKEUPEMAIL = 10055;

	public static ActivityDailyTypeMgr getInstance() {
		return instance;
	}

	public void synCountTypeData(Player player) {
		ActivityDailyTypeItemHolder.getInstance().synAllData(player);
	}



	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);	
		checkOtherDay(player);
		checkClose(player);

	}
	
	/**
	 * 
	 * @param player  
	 * 配表如果同时开启，则会add第一个生效的数据，风险较低，需要一个检查配置的方法
	 */
	private void checkNewOpen(Player player) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeCfg> activityDailyTypeCfgList = ActivityDailyTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityDailyTypeCfg cfg : activityDailyTypeCfgList){
			if(!isOpen(cfg)){
				continue;
			}
			ActivityDailyTypeItem targetItem = dataHolder.getItem(player.getUserId());
			if(targetItem == null){
				targetItem = ActivityDailyTypeCfgDAO.getInstance().newItem(player,cfg);
				dataHolder.addItem(player, targetItem);
			}			
		}
	}
	
	public boolean isOpen(ActivityDailyTypeCfg activityCountTypeCfg) {

		if (activityCountTypeCfg != null) {
			long startTime = activityCountTypeCfg.getStartTime();
			long endTime = activityCountTypeCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}	
	
	private void checkCfgVersion(Player player) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityDailyTypeItem targetItem : itemList) {
			ActivityDailyTypeCfg targetCfg = ActivityDailyTypeCfgDAO.getInstance().getCfgByItemOfEnumId(targetItem);
			if(targetCfg == null){
//				GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
				continue;
			}					
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}		
	}
	
	private void checkOtherDay(Player player) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeItem> item = dataHolder.getItemList(player.getUserId());
		
		for (ActivityDailyTypeItem targetItem : item) {
			ActivityDailyTypeCfg targetCfg = ActivityDailyTypeCfgDAO.getInstance().getConfig(targetItem.getCfgid());
			if(targetCfg == null){
				GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
				continue;
			}
			if(ActivityTypeHelper.isNewDayHourOfActivity(5,targetItem.getLastTime())){
				sendEmailIfGiftNotTaken(player, targetItem.getSubItemList() );
				targetItem.reset(targetCfg);
				dataHolder.updateItem(player, targetItem);
			}
		}
	}
		
	private void checkClose(Player player) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		List<ActivityDailyTypeItem> itemList = dataHolder.getItemList(player.getUserId());

		for (ActivityDailyTypeItem activityDailyCountTypeItem : itemList) {// 每种活动
			if (isClose(activityDailyCountTypeItem)&&!activityDailyCountTypeItem.isClosed()) {
				sendEmailIfGiftNotTaken(player,  activityDailyCountTypeItem.getSubItemList());
				activityDailyCountTypeItem.setClosed(true);
				activityDailyCountTypeItem.setTouchRedPoint(true);
				dataHolder.updateItem(player, activityDailyCountTypeItem);
			}
		}
	}
	
	private boolean isClose(ActivityDailyTypeItem activityDailyCountTypeItem) {
	if (activityDailyCountTypeItem != null) {
		ActivityDailyTypeCfg cfgById = ActivityDailyTypeCfgDAO.getInstance().getCfgById(activityDailyCountTypeItem.getCfgid());
		if(cfgById!=null){
			long endTime = cfgById.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime > endTime;
		}else{
			GameLog.error("activitydailycounttypemgr","" , "配置文件找不到数据奎对应的活动"+ ActivityDailyTypeEnum.Daily);
		}
	}
	return false;
}
	
	
	private void sendEmailIfGiftNotTaken(Player player,
			List<ActivityDailyTypeSubItem> subItemList) {
		for (ActivityDailyTypeSubItem subItem : subItemList) {// 配置表里的每种奖励
			ActivityDailyTypeSubCfg subItemCfg = ActivityDailyTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());
			if(subItemCfg == null){
				GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
				return;
			}
			if (subItem.getCount() >= subItemCfg.getCount()&&!subItem.isTaken()) {
				boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getGiftId(), MAKEUPEMAIL + "",subItemCfg.getEmailTitle());
				subItem.setTaken(true);
				if (!isAdd) 
					GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "通用活动关闭后未领取奖励获取邮件内容失败", null);
				}
		}		
	}
	
	/**
	 * 
	 * @param player
	 * @return 配置同时开启两个活动，则会出现a可以开启但b不能开启的情况，而子活动又是属于b的话，会造成add没有subitem;需要检查配置的方法
	 */
	public boolean isLevelEnough(Player player) {
		List<ActivityDailyTypeCfg> cfgList = ActivityDailyTypeCfgDAO.getInstance().getAllCfg();
		for(ActivityDailyTypeCfg cfg: cfgList){
			if(player.getLevel() >= cfg.getLevelLimit()&&isOpen(cfg)){
				return true;
			}			
		}	
		return false;
	}
	
	/**用于验证单个cfg是否开启*/
	public boolean isOpen(ActivityDailyTypeSubCfg activityCountTypesubCfg) {		
		if (activityCountTypesubCfg != null) {			
			long startTime = activityCountTypesubCfg.getStartTime();
			long endTime = activityCountTypesubCfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime > startTime;
		}
		return false;
	}
	
	/**用于验证某个功能对应的多个subCfg是否存在开启的*/
	public boolean isOpen(List<ActivityDailyTypeSubCfg> subList) {
		boolean isOpen = false;
		for(ActivityDailyTypeSubCfg subCfg:subList){
			if(isOpen(subCfg)){
				isOpen = true;
				break;
			}
		}
		return isOpen;
	}
	
	
	
	public void addCount(Player player, ActivityDailyTypeEnum countType, int countadd) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "枚举获得了活动且判断开启，但活动未生成item;主活动未开启，子活动时间超期", null);
			return;
		}
		ActivityDailyTypeSubItem subItem = getbyDailyCountTypeEnum(player, countType, dataItem);
		if(subItem == null){
			GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "枚举获得了活动且判断开启，但活动未生成对应subitem;主活动开启，其他主活动的子活动跨期进入", null);
			return;
		}
		subItem.setCount(subItem.getCount() + countadd);
		dataHolder.updateItem(player, dataItem);
	}
	
	public ActivityDailyTypeSubItem getbyDailyCountTypeEnum (Player player,ActivityDailyTypeEnum typeEnum,ActivityDailyTypeItem dataItem){		
		ActivityDailyTypeSubItem subItem = null;
		ActivityDailyTypeSubCfg cfg = null;
		List<ActivityDailyTypeSubCfg> subcfglist = ActivityDailyTypeSubCfgDAO.getInstance().getAllCfg();
		for(ActivityDailyTypeSubCfg subcfg :subcfglist){
			if(StringUtils.equals(subcfg.getEnumId(), typeEnum.getCfgId())&&isOpen(subcfg)){
				cfg = subcfg;
			}
		}
				
		if(cfg == null){
			GameLog.error("Activitydailycounttypemgr", "uid=" + player.getUserId(), "事件判断活动开启中,但活动配置生成的cfg没有对应的事件枚举");
			return subItem;
		}
		
		if(dataItem != null){
			List<ActivityDailyTypeSubItem> sublist = dataItem.getSubItemList();
			for(ActivityDailyTypeSubItem subitem : sublist){
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
	
	
	
	public ActivityComResult takeGift(Player player, ActivityDailyTypeEnum countType, String subItemId) {
		ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();

		ActivityDailyTypeItem dataItem = dataHolder.getItem(player.getUserId());
		ActivityComResult result = ActivityComResult.newInstance(false);

		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");

		} else {
			ActivityDailyTypeSubItem targetItem = null;

			List<ActivityDailyTypeSubItem> subItemList = dataItem.getSubItemList();
			for (ActivityDailyTypeSubItem itemTmp : subItemList) {
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

	private void takeGift(Player player, ActivityDailyTypeSubItem targetItem) {
		ActivityDailyTypeSubCfg subCfg = ActivityDailyTypeSubCfgDAO.getInstance().getById(targetItem.getCfgId());
		if(subCfg == null){
			GameLog.error(LogModule.ComActivityDailyCount, null, "通用活动找不到配置文件", null);
			return;
		}
		targetItem.setTaken(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getGiftId());

	}

	@Override
	public void updateRedPoint(Player player, String eNum) {
		ActivityDailyTypeItemHolder activityCountTypeItemHolder = new ActivityDailyTypeItemHolder();
		ActivityDailyTypeCfg cfg = ActivityDailyTypeCfgDAO.getInstance().getCfgById(eNum);
		if(cfg == null ){
			return;
		}
		ActivityDailyTypeEnum dailyEnum = ActivityDailyTypeEnum.getById(cfg.getEnumId());
		if(dailyEnum == null){
			GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动枚举", null);
			return;
		}
		ActivityDailyTypeItem dataItem = activityCountTypeItemHolder.getItem(player.getUserId());
		if(dataItem == null){
			GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "心跳传入id获得的页签枚举无法找到活动数据", null);
			return;
		}
		if(!dataItem.isTouchRedPoint()){
			dataItem.setTouchRedPoint(true);
			activityCountTypeItemHolder.updateItem(player, dataItem);
		}
		
	}

}
