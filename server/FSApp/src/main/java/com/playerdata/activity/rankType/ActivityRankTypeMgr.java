package com.playerdata.activity.rankType;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfgDAO;
import com.playerdata.activity.rankType.data.ActivityRankTypeEntry;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItemHolder;
import com.playerdata.activity.rankType.data.ActivityRankTypeUserInfo;


public class ActivityRankTypeMgr {
	
	private static ActivityRankTypeMgr instance = new ActivityRankTypeMgr();
	
	private final static int MAKEUPEMAIL = 10055;
	
	public static ActivityRankTypeMgr getInstance(){
		return instance;
	}
	
	public void synRankTypeData(Player player){
		ActivityRankTypeItemHolder.getInstance().synAllData(player);
	}
	/**登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空*/
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);		
		checkClose(player);
		
	}

	private void checkNewOpen(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		List<ActivityRankTypeCfg> allCfgList = ActivityRankTypeCfgDAO.getInstance().getAllCfg();
		for (ActivityRankTypeCfg activityRankTypeCfg : allCfgList) {//遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if(isOpen(activityRankTypeCfg)){
				ActivityRankTypeEnum RankTypeEnum = ActivityRankTypeEnum.getById(activityRankTypeCfg.getId());
				if(RankTypeEnum != null){
					ActivityRankTypeItem targetItem = dataHolder.getItem(player.getUserId(), RankTypeEnum);//已在之前生成数据的活动
					if(targetItem != null){
						if(targetItem.isClosed()){
							dataHolder.removeItem(player, RankTypeEnum);
							
						}
					}
					
					if(targetItem == null){
						targetItem = ActivityRankTypeCfgDAO.getInstance().newItem(player, RankTypeEnum);//生成新开启活动的数据
						if(targetItem!=null){
							dataHolder.addItem(player, targetItem);
						}
					}
				}
				
				
			}
		}
	}
	private void checkClose(Player player) {
		ActivityRankTypeItemHolder dataHolder = ActivityRankTypeItemHolder.getInstance();
		List<ActivityRankTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		
		for (ActivityRankTypeItem activityRankTypeItem : itemList) {//每种活动
			if(isClose(activityRankTypeItem)){
										
				activityRankTypeItem.setClosed(true);
				dataHolder.updateItem(player, activityRankTypeItem);
			}
		}

		
	}

	
	public boolean isClose(ActivityRankTypeItem activityRankTypeItem) {
		
		ActivityRankTypeCfg cfgById = ActivityRankTypeCfgDAO.getInstance().getCfgById(activityRankTypeItem.getCfgId());
		
		long endTime = cfgById.getEndTime();		
		long currentTime = System.currentTimeMillis();

		return currentTime > endTime;
	}
	
	
	public boolean isOpen(ActivityRankTypeCfg activityRankTypeCfg) {
		
		long startTime = activityRankTypeCfg.getStartTime();
		long endTime = activityRankTypeCfg.getEndTime();		
		long currentTime = System.currentTimeMillis();
		
		return currentTime < endTime && currentTime > startTime;
	}
	
	public List<ActivityRankTypeEntry> getRankList(ActivityRankTypeEnum rankType, int offset, int limit){		
		return new ArrayList<ActivityRankTypeEntry>();
	}
	
	public ActivityRankTypeUserInfo getUserInfo(Player player, ActivityRankTypeEnum rankType){		
		return null;
	}

	public void sendGift(){
		
	}
	

}
