package com.bm.targetSell.param;

import java.util.ArrayList;
import java.util.List;

public class TargetSellRoleChange {
	private String userId;
	private long startTime;
	private List<ERoleAttrs> changeList;
	
	public TargetSellRoleChange(String userId, long startTime){
		this.userId = userId;
		this.startTime = startTime;
		this.changeList = new ArrayList<ERoleAttrs>();
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
	public List<ERoleAttrs> getChangeList() {
		return changeList;
	}
	public void setChangeList(List<ERoleAttrs> changeList) {
		this.changeList = changeList;
	}
	public void addChange(List<ERoleAttrs> value){
		for (ERoleAttrs eRoleAttrs : value) {
			if(!this.changeList.contains(eRoleAttrs)){
				this.changeList.add(eRoleAttrs);
			}
		}
		
	}
	
}
