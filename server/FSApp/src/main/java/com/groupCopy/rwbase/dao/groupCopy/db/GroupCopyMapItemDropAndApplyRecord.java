package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.LinkedList;


/**
 * 帮派副本地图掉落单个奖励物品对应申请列表
 * @author Alex
 * 2016年6月12日 下午4:54:00
 */
public class GroupCopyMapItemDropAndApplyRecord {

	int itemID;
	
	private LinkedList<DropInfo> dropInfoList = new LinkedList<DropInfo>();
	
	
	
	public GroupCopyMapItemDropAndApplyRecord(int itemID) {
		this.itemID = itemID;
	}

	public void addDropItem(int count) {
		dropInfoList.add(new DropInfo(count, System.currentTimeMillis()));
	}

}
