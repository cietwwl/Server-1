package com.rwbase.dao.guide.pojo;

import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user_guide_progress")
public class UserGuideProgress {

	@Id
	private String userId;
	private ConcurrentHashMap<Integer, Integer> progressMap;

	public UserGuideProgress() {
		this.progressMap = new ConcurrentHashMap<Integer, Integer>(16, 1.0f, 1);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ConcurrentHashMap<Integer, Integer> getProgressMap() {
		return progressMap;
	}

	public void setProgressMap(ConcurrentHashMap<Integer, Integer> progressMap) {
		this.progressMap = progressMap;
	}

}
