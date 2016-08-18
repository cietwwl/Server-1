package com.rwbase.dao.copy.itemPrivilege;

public class PrivilegeDescItem {
	
	private int itemID;		//id号，对应ItemInfo中的id
	
	private float value = 1f;		//加成值
	
	private boolean isPersent = true;	//是否百分比（百分比和绝对数值）
	
	private boolean isAllIDHave = false;	//是否对所有的id号都有效

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public boolean isPersent() {
		return isPersent;
	}

	public void setPersent(boolean isPersent) {
		this.isPersent = isPersent;
	}

	public boolean isAllIDHave() {
		return isAllIDHave;
	}

	public void setAllIDHave(boolean isAllIDHave) {
		this.isAllIDHave = isAllIDHave;
	}

}
