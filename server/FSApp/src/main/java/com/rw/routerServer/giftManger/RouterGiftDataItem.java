package com.rw.routerServer.giftManger;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouterGiftDataItem implements RoleExtProperty{
	@Id
	private Integer id;
	@OwnerId
	private String userId;

	@CombineSave
	private String belongTime;	//礼包所属于的日期
	@CombineSave
	private int count;



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
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
