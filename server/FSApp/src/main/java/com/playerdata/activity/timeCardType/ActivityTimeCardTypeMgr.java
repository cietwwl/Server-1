package com.playerdata.activity.timeCardType;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;


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
		for (ActivityTimeCardTypeCfg ActivityTimeCardTypeCfg : allCfgList) {
			
			ActivityTimeCardTypeEnum typeEnum = ActivityTimeCardTypeEnum.getById(ActivityTimeCardTypeCfg.getId());
			if(typeEnum != null){
				ActivityTimeCardTypeItem targetItem = dataHolder.getItem(player.getUserId(), typeEnum);//已在之前生成数据的活动
				if(targetItem == null){
					ActivityTimeCardTypeItem newItem = ActivityTimeCardTypeCfgDAO.getInstance().newItem(player, typeEnum);
					dataHolder.addItem(player, newItem);
				}
				
			}
		}
	}



}
