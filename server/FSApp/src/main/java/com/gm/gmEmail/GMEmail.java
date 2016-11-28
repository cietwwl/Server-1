package com.gm.gmEmail;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "server_gmemail")
public class GMEmail {
	@Id
	private String userId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	private List<Long> taskIdList = new ArrayList<Long>();
	
	
	public List<Long> getTaskIdList() {
		return taskIdList;
	}
	public void setTaskIdList(List<Long> taskIdList) {
		this.taskIdList = taskIdList;
	} 
	
	
}
