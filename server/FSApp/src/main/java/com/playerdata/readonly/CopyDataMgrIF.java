package com.playerdata.readonly;

import java.util.List;

/*
 * 副本数据管理接口
 */

public interface CopyDataMgrIF {
	public CopyDataIF getByInfoId(int infoId);
	
	public int getCopyCount(String strLevelId);
	
	//首次通关奖励
	public List<ItemInfoIF> checkFirstPrize(int copyType,String levelId);
}
