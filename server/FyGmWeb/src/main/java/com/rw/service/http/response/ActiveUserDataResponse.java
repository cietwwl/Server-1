package com.rw.service.http.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

public class ActiveUserDataResponse implements Serializable{
	private static final long serialVersionUID = -5182532647273100001L;
	
	private long createTime;
	private int createNum;
	private int activeNum;
	
	@Transient
	private List<String> userIdList = new ArrayList<String>();
	
	public ActiveUserDataResponse(){
		
	}
	
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getCreateNum() {
		return createNum;
	}
	public void setCreateNum(int createNum) {
		this.createNum = createNum;
	}
	public int getActiveNum() {
		return activeNum;
	}
	public void setActiveNum(int activeNum) {
		this.activeNum = activeNum;
	}
	public void addActiveNum(){
		this.activeNum++;
	}
	public void addCreateNum(){
		this.createNum++;
	}
}
