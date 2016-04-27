package com.playerdata.activity.timeCardType;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;


public class ActivityTimeCardTypeMgr {
	
	private static ActivityTimeCardTypeMgr instance = new ActivityTimeCardTypeMgr();
	
	public static ActivityTimeCardTypeMgr getInstance(){
		return instance;
	}
	
	public void synCountTypeData(Player player){
		ActivityTimeCardTypeItemHolder.getInstance().synAllData(player);
	}
	
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);		
	}

	private void checkNewOpen(Player player) {
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();
		List<ActivityTimeCardTypeCfg> allCfgList = ActivityTimeCardTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityTimeCardTypeCfg activityTimeCardTypeCfg : allCfgList) {
			
			ActivityTimeCardTypeEnum typeEnum = ActivityTimeCardTypeEnum.getById(activityTimeCardTypeCfg.getId());
			if(typeEnum != null){
				ActivityTimeCardTypeItem targetItem = dataHolder.getItem(player.getUserId(), typeEnum);//已在之前生成数据的活动
				if(targetItem == null){
					ActivityTimeCardTypeItem newItem = ActivityTimeCardTypeCfgDAO.getInstance().newItem(player, typeEnum);
					dataHolder.addItem(player, newItem);
				}
				
			}
		}
	}
	
	public boolean isTimeCardOnGoing(Player player, String timeCardTypeCfgId, String timeCardTypeSubItemCfgId){
		
		boolean isTimeCardOnGoing = false;
		ActivityTimeCardTypeSubItem targetSubItem = null;
		targetSubItem = getSubItem(player, timeCardTypeCfgId,timeCardTypeSubItemCfgId);
		if(targetSubItem!=null){
			isTimeCardOnGoing = targetSubItem.getDayLeft() >= 0;
		}
		return isTimeCardOnGoing;
	}

	private ActivityTimeCardTypeSubItem getSubItem(Player player, String timeCardTypeCfgId, String timeCardTypeSubItemCfgId) {
		ActivityTimeCardTypeSubItem targetSubItem = null;
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();
		
		ActivityTimeCardTypeEnum typeEnum = ActivityTimeCardTypeEnum.getById(timeCardTypeCfgId);
		if(typeEnum != null){
			ActivityTimeCardTypeItem targetItem = dataHolder.getItem(player.getUserId(), typeEnum);//已在之前生成数据的活动
			if(targetItem != null){
				List<ActivityTimeCardTypeSubItem> subItemList = targetItem.getSubItemList();
				for (ActivityTimeCardTypeSubItem activityTimeCardTypeSubItem : subItemList) {
					if(StringUtils.equals(timeCardTypeSubItemCfgId, activityTimeCardTypeSubItem.getId())){
						targetSubItem = activityTimeCardTypeSubItem;
						break;
					}
				}
			}
			
		}
		return targetSubItem;
	}



}
