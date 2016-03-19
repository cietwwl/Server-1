package com.rw.service.http.platformResponse;

import java.io.Serializable;

public class PlatformNoticeBaseDataResponse  implements Serializable{
	private static final long serialVersionUID = -6182532647273100002L;
	
	private String title;
	private String content;
	private long startTime;
	private long endTime;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
