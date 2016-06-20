package com.groupCopy.rwbase.dao.groupCopy.db;

/**
 * 分配记录模板
 * 
 * @author Alex
 * 2016年6月17日 下午2:13:20
 */
public class DistRewRecordItem {

	//奖励的道具id
	private int itemID;
	//获得奖励的角色名
	private String roleName;
	//获得奖励的时间点
	private String timeStr;
	//获得方式
	private String getType;
	
	
	public DistRewRecordItem(int itemID, String roleName, String timeStr,
			String getType) {
		super();
		this.itemID = itemID;
		this.roleName = roleName;
		this.timeStr = timeStr;
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


	public String getTimeStr() {
		return timeStr;
	}


	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}


	public String getGetType() {
		return getType;
	}


	public void setGetType(String getType) {
		this.getType = getType;
	}
	
	
}
