package com.rw.routerServer.giftManger;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

public class RouterGiftDataItem implements RoleExtProperty{
	
	private Integer giftId;
	
	private String userId;
	
	private String belongTime;	//礼包所属于的日期
	
	private int count;

	public Integer getGiftId() {
		return giftId;
	}

	public void setGiftId(Integer giftId) {
		this.giftId = giftId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBelongTime() {
		return belongTime;
	}

	public void setBelongTime(String belongTime) {
		this.belongTime = belongTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public Integer getId() {
		return giftId;
	}
}
