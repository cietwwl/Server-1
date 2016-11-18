package com.bm.targetSell.param;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TargetSellRoleChange {
	private String userId;
	private long startTime;
	private List<String> changeList;

	public TargetSellRoleChange(String userId, long startTime) {
		this.userId = userId;
		this.startTime = startTime;
		this.changeList = new ArrayList<String>();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public List<String> getChangeList() {
		return changeList;
	}

	public void setChangeList(List<String> changeList) {
		this.changeList = changeList;
	}


	public void addChange(String value) {
		if (!this.changeList.contains(value)) {
			this.changeList.add(value);
		}
	}

}
