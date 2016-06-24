package com.playerdata.activity.dateType;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dateType.cfg.ActivityDateTypeCfg;
import com.playerdata.activity.dateType.cfg.ActivityDateTypeCfgDAO;
import com.playerdata.activity.dateType.cfg.ActivityDateTypeSubCfg;
import com.playerdata.activity.dateType.cfg.ActivityDateTypeSubCfgDAO;
import com.playerdata.activity.dateType.data.ActivityDateTypeItem;
import com.playerdata.activity.dateType.data.ActivityDateTypeItemHolder;
import com.playerdata.activity.dateType.data.ActivityDateTypeSubItem;


public class ActivityDateTypeMgr {
	
	private static ActivityDateTypeMgr instance = new ActivityDateTypeMgr();
	
	private final static int MAKEUPEMAIL = 10055;
	
	public static ActivityDateTypeMgr getInstance(){
		return instance;
	}
	
	public void synDateTypeData(Player player){
		ActivityDateTypeItemHolder.getInstance().synAllData(player);
	}
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);		
		checkClose(player);
		
	}

	private void checkNewOpen(Player player) {
		ActivityDateTypeItemHolder dataHolder = ActivityDateTypeItemHolder.getInstance();
		List<ActivityDateTypeCfg> allCfgList = ActivityDateTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityDateTypeCfg activityDateTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityDateTypeCfg)){
				ActivityDateTypeEnum DateTypeEnum = ActivityDateTypeEnum.getById(activityDateTypeCfg.getId());
				if(DateTypeEnum != null){
					ActivityDateTypeItem targetItem = dataHolder.getItem(player.getUserId(), DateTypeEnum);//已在之前生成数据的活动
					if(targetItem != null){
						if(targetItem.isClosed()){
							dataHolder.removeItem(player, DateTypeEnum);
							
						}
					}
					
					if(targetItem == null){
						targetItem = ActivityDateTypeCfgDAO.getInstance().newItem(player, DateTypeEnum);//生成新开启活动的数据
						if(targetItem!=null){
							dataHolder.addItem(player, targetItem);
						}
					}
				}
				
				
			}
		}
	}
	private void checkClose(Player player) {
		ActivityDateTypeItemHolder dataHolder = ActivityDateTypeItemHolder.getInstance();
		List<ActivityDateTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		
		for (ActivityDateTypeItem activityDateTypeItem : itemList) {//每种活动
			if(isClose(activityDateTypeItem)){
				List<ActivityDateTypeSubItem>  subItemList = activityDateTypeItem.getSubItemList();
				for(ActivityDateTypeSubItem subItem : subItemList){
					ActivityDateTypeSubCfg subItemCfg = ActivityDateTypeSubCfgDAO.getInstance().getCfgById(subItem.getCfgId());
					
					if(!subItem.isTaken() && subItem.getCount() >= subItemCfg.getAwardCount()){
						boolean isAdd = ComGiftMgr.getInstance().addGiftTOEmailById(player, subItemCfg.getAwardGift(), MAKEUPEMAIL+"","");	
						if (isAdd) {
							subItem.setTaken(true);
						} else {
							GameLog.error(LogModule.ComActivityDate, player.getUserId(),"通用活动关闭后发送未领取奖励邮件失败。",null);
						}						
					}									
				}						
				activityDateTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityDateTypeItem);
			}
		}

		
	}

	
	public boolean isClose(ActivityDateTypeItem activityDateTypeItem) {
		
		ActivityDateTypeCfg cfgById = ActivityDateTypeCfgDAO.getInstance().getCfgById(activityDateTypeItem.getCfgId());
		
		long endTime = cfgById.getEndTime();		
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}
	
	
	public boolean isOpen(ActivityDateTypeCfg activityDateTypeCfg) {
		
		long startTime = activityDateTypeCfg.getStartTime();
		long endTime = activityDateTypeCfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		
		return currentTime < endTime && currentTime > startTime;
	}

	public void addCount(Player player, ActivityDateTypeEnum DateType,int countadd){
		ActivityDateTypeItemHolder dataHolder = ActivityDateTypeItemHolder.getInstance();
		
		ActivityDateTypeItem dataItem = dataHolder.getItem(player.getUserId(), DateType);
		dataItem.setCount(dataItem.getCount()+countadd);
		
		ActivityDateTypeSubItem currentSubItem = dataItem.getCurentDaySubItem();
		currentSubItem.setCount(currentSubItem.getCount()+1);
		
		dataHolder.updateItem(player, dataItem);
	}
	




	public ActivityComResult takeGift(Player player, ActivityDateTypeEnum DateType, String subItemId){
		ActivityDateTypeItemHolder dataHolder = ActivityDateTypeItemHolder.getInstance();
		
		ActivityDateTypeItem dataItem = dataHolder.getItem(player.getUserId(), DateType);
		ActivityComResult result = ActivityComResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("活动尚未开启");
			
		}else{			
			ActivityDateTypeSubItem targetItem = dataItem.getSubItemById(subItemId);			
		
			ActivityDateTypeSubCfg subItemCfg = ActivityDateTypeSubCfgDAO.getInstance().getById(subItemId);
			
			
			if(targetItem == null){
				result.setReason("该奖励不存在 id:"+DateType.getCfgId());
			}else if(subItemCfg.getDay() > dataItem.getDay()){
				result.setReason("未到领奖时间");
			}else if(targetItem.isTaken()){
				//申请已领取过的奖励
				result.setReason("奖励已领取");
			}else{
				takeGift(player,targetItem);			
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}
			
		}
		
		
		return result;
	}
	public ActivityComResult takeBigGift(Player player, ActivityDateTypeEnum DateType, String subItemId){
		ActivityDateTypeItemHolder dataHolder = ActivityDateTypeItemHolder.getInstance();
		
		ActivityDateTypeItem dataItem = dataHolder.getItem(player.getUserId(), DateType);
		ActivityComResult result = ActivityComResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("活动尚未开启");
			
		}else{			
			ActivityDateTypeCfg itemCfg = ActivityDateTypeCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
			if(dataItem.getCount() < itemCfg.getAwardDount()){
				result.setReason("不符合领奖条件");
			}else if(dataItem.isTaken()){
				//申请已领取过的奖励
				result.setReason("奖励已领取");
			}else{
				takeGift(player,dataItem);			
				result.setSuccess(true);
				dataHolder.updateItem(player, dataItem);
			}
			
		}
		
		
		return result;
	}

	private  void takeGift(Player player,ActivityDateTypeSubItem targetItem) {
		ActivityDateTypeSubCfg subItemCfg = ActivityDateTypeSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
		String gift = subItemCfg.getAwardGift();
		targetItem.setTaken(true);
		targetItem.getCount();
		ComGiftMgr.getInstance().addGiftById(player, gift);	
	}
	
	private  void takeGift(Player player,ActivityDateTypeItem targetItem) {
		ActivityDateTypeCfg itemCfg = ActivityDateTypeCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
		String gift = itemCfg.getAwardGift();
		targetItem.setTaken(true);
		targetItem.getCount();
		ComGiftMgr.getInstance().addGiftById(player, gift);	
	}
	
	

}
