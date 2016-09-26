package com.playerdata.readonly;


/*
 * 副本数据管理接口
 */

public interface CopyDataMgrIF {
	public CopyDataIF getByInfoId(int infoId);
	
	public int getCopyCount(String strLevelId);
	
}
