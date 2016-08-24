package com.playerdata.activity.limitHeroType;


import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfg;
import com.playerdata.activity.limitHeroType.cfg.ActivityLimitHeroCfgDAO;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItemHolder;

public class ActivityLimitHeroTypeMgr {
	private static ActivityLimitHeroTypeMgr instance = new ActivityLimitHeroTypeMgr();
	public static ActivityLimitHeroTypeMgr getInstance (){
		return instance;
	}
	
	public void synCountTypeData(Player player) {
		ActivityLimitHeroTypeItemHolder.getInstance().synAllData(player);
	}
	
	/** 登陆或打开活动入口时，核实所有活动是否开启，并根据活动类型生成空的奖励数据;如果活动为重复的,如何在活动重复时晴空 */
	public void checkActivityOpen(Player player) {
		checkNewOpen(player);
		checkCfgVersion(player);
		checkClose(player);
	}
	
	

	private void checkNewOpen(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroCfg> allCfgList = ActivityLimitHeroCfgDAO.getInstance().getAllCfg();
		for (ActivityLimitHeroCfg cfg : allCfgList) {// 遍历种类*各类奖励数次数,生成开启的种类个数空数据
			if (!isOpen(cfg)) {
				// 活动未开启
				continue;
			}			
			ActivityLimitHeroTypeItem targetItem = dataHolder.getItem(player.getUserId());// 已在之前生成数据的活动
			if (targetItem != null) {					
				continue;
			}
			targetItem = ActivityLimitHeroCfgDAO.getInstance().newItem(player, cfg);// 生成新开启活动的数据
			if(targetItem == null){
				continue;
			}
			dataHolder.addItem(player, targetItem);
		}
	}

	public boolean isOpen(ActivityLimitHeroCfg cfg) {
		if (cfg != null) {
			long startTime = cfg.getStartTime();
			long endTime = cfg.getEndTime();
			long currentTime = System.currentTimeMillis();
			return currentTime < endTime && currentTime >= startTime;
		}
		return false;
	}
	
	private void checkCfgVersion(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for (ActivityLimitHeroTypeItem targetItem : itemList) {			
			ActivityLimitHeroCfg targetCfg = ActivityLimitHeroCfgDAO.getInstance().getCfgListByItem(targetItem);
			if(targetCfg == null){
				continue;
			}			
			
			if (!StringUtils.equals(targetItem.getVersion(), targetCfg.getVersion())) {
				targetItem.reset(targetCfg,ActivityLimitHeroCfgDAO.getInstance().newSubItemList(targetCfg));
				dataHolder.updateItem(player, targetItem);
			}
		}
	}

	private void checkClose(Player player) {
		ActivityLimitHeroTypeItemHolder dataHolder = ActivityLimitHeroTypeItemHolder.getInstance();
		List<ActivityLimitHeroTypeItem> itemList = dataHolder.getItemList(player.getUserId());
		for(ActivityLimitHeroTypeItem item : itemList){
			if(item.isClosed()){
				continue;			
			}
			ActivityLimitHeroCfg cfg =ActivityLimitHeroCfgDAO.getInstance().getCfgById(item.getCfgId());
			if(cfg == null){
				GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "玩家登录时服务器配置表已更新，只能通过版本核实来刷新数据", null);
				continue;
			}
			if(isOpen(cfg)){
				continue;
			}
			checkRankRewards(player,item);//邮件派发排行奖励
			//补发sendmail;
			item.setClosed(true);
			item.setTouchRedPoint(true);
			dataHolder.updateItem(player, item);			
		}
	}

	private void checkRankRewards(Player player, ActivityLimitHeroTypeItem item) {
		// TODO Auto-generated method stub
		
	}	
	
	
	
	
	
	
	
}
