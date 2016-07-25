package com.groupCopy.rwbase.dao.groupCopy.db;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 分配记录模板
 * 
 * @author Alex
 * 2016年6月17日 下午2:13:20
 */
@SynClass
public class DistRewRecordItem {

	//奖励的道具id
	private int itemID;
	//获得奖励的角色名
	private String roleName;
	//获得奖励的时间点
	private long captureTime;
	//获得方式
	private String getType;
	
	
	public DistRewRecordItem() {
	}


	public DistRewRecordItem(int itemID, String roleName, long time,
			String getType) {
		super();
		this.itemID = itemID;
		this.roleName = roleName;
		this.captureTime = time;
		this.getType = getType;
	}


	public int getItemID() {
		return itemID;
	}


	public void setItemID(int itemID) {
		this.itemID = itemID;
	}


	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	public long getCaptureTime() {
		return captureTime;
	}


	public void setCaptureTime(long captureTime) {
		this.captureTime = captureTime;
	}


	public String getGetType() {
		return getType;
	}


	public void setGetType(String getType) {
		this.getType = getType;
	}
	
	
}
