package com.rwbase.dao.guide.pojo;

import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;
@Table(name = "user_plot_progress")
public class UserPlotProgress {

	@Id
	private String userId;
	private ConcurrentHashMap<String, Integer> progressMap;

	public UserPlotProgress(){
		this.progressMap = new ConcurrentHashMap<String, Integer>();
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ConcurrentHashMap<String, Integer> getProgressMap() {
		return progressMap;
	}

	public void setProgressMap(ConcurrentHashMap<String, Integer> progressMap) {
		this.progressMap = progressMap;
	}

}
