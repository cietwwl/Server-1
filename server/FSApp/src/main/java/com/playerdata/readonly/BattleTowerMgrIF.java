package com.playerdata.readonly;

import com.rwbase.dao.battletower.pojo.readonly.TableBattleTowerIF;

/*
 * @author HC
 * @date 2015年9月1日 下午3:39:44
 * @Description 
 */
public interface BattleTowerMgrIF {
	/**
	 * 获取试练塔数据的模版
	 * 
	 * @return
	 */
	public TableBattleTowerIF getTableBattleTower();
}