package com.playerdata.activity.rateType;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItemHolder;


public class ActivityRateTypeMgr {
	
	private static ActivityRateTypeMgr instance = new ActivityRateTypeMgr();
	
	public static ActivityRateTypeMgr getInstance(){
		return instance;
	}
	
	public void synData(Player player){
		ActivityRateTypeItemHolder.getInstance().synAllData(player);
	}
	
	public boolean isActivityOnGoing(Player player, ActivityRateTypeEnum activityRateTypeEnum){
		
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder.getInstance();
		
		ActivityRateTypeItem targetItem = dataHolder.getItem(player.getUserId(), activityRateTypeEnum);//已在之前生成数据的活动
		
		return targetItem!=null && !targetItem.isClosed();
	}
	
	public float getRate(ActivityRateTypeEnum activityRateTypeEnum){
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance().getCfgById(activityRateTypeEnum.getCfgId());
		return cfgById==null? 1 : cfgById.getRate();
	}

	
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivity(Player player) {
		checkNewOpen(player);		
		checkClose(player);
		
	}

	private void checkNewOpen(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder.getInstance();
		List<ActivityRateTypeCfg> allCfgList = ActivityRateTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRateTypeCfg activityRateTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityRateTypeCfg)){
				ActivityRateTypeEnum typeEnum = ActivityRateTypeEnum.getById(activityRateTypeCfg.getId());
				if(typeEnum != null){
					ActivityRateTypeItem targetItem = dataHolder.getItem(player.getUserId(), typeEnum);//已在之前生成数据的活动
					
					if(targetItem == null){
						targetItem = ActivityRateTypeCfgDAO.getInstance().newItem(player, typeEnum);//生成新开启活动的数据
						if(targetItem!=null){
							dataHolder.addItem(player, targetItem);
						}
					}
				}
				
				
			}
		}
	}
	private void checkClose(Player player) {
		ActivityRateTypeItemHolder dataHolder = ActivityRateTypeItemHolder.getInstance();
		List<ActivityRateTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		
		for (ActivityRateTypeItem activityRateTypeItem : itemList) {//每种活动
			if(isClose(activityRateTypeItem)){								
				activityRateTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityRateTypeItem);
			}
		}
		
	}
	
	private boolean isClose(ActivityRateTypeItem ActivityRateTypeItem) {
		
		ActivityRateTypeCfg cfgById = ActivityRateTypeCfgDAO.getInstance().getCfgById(ActivityRateTypeItem.getCfgId());
		
		long endTime = cfgById.getEndTime();		
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}
	
	
	private boolean isOpen(ActivityRateTypeCfg ActivityRateTypeCfg) {
		
		long startTime = ActivityRateTypeCfg.getStartTime();
		long endTime = ActivityRateTypeCfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		return currentTime < endTime && currentTime > startTime;
	}


}
