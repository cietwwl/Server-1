package com.rwbase.dao.openLevelTiggerService.IServiceTiggerHandler;

import com.playerdata.Player;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.openLevelTiggerService.CfgOpenLevelTiggerServiceDAO;

public interface IServiceTiggerHandler {

	/*
	 * 提升等级时，向对应的功能数据增加初始的等级引导数据
	 */
	public void openLevelToCreatItem(Long now,String userId,CfgOpenLevelLimit cfg,CfgOpenLevelTiggerServiceDAO cfgServiceDao);

	
	
	/*
	 * 计时器对各不同等级激活的功能进行触发判断；并执行逻辑
	 */
	public void doActionByTimerManager(Player player);
	
	
	
}
