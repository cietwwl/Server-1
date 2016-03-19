package com.rwbase.dao.dropitem;

import com.rwbase.dao.copy.pojo.ItemInfo;

public class DropItemInfo extends ItemInfo {

	private Integer dropRecordId;

	public DropItemInfo() {
	}

	public DropItemInfo(int dropRecordId) {
		this.dropRecordId = dropRecordId;
	}

	public Integer getDropRecordId() {
		return dropRecordId;
	}

	public void setDropRecordId(Integer dropRecordId) {
		this.dropRecordId = dropRecordId;
	}
}
