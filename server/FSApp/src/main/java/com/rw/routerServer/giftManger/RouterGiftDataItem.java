package com.rw.routerServer.giftManger;

import javax.persistence.Id;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;

public class RouterGiftDataItem implements RoleExtProperty{
	@Id
	private Integer giftId;
	
	private String userId;

	@CombineSave
	private String belongTime;	//礼包所属于的日期
	@CombineSave
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
