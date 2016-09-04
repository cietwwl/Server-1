package com.rwbase.dao.serverData;

import com.rw.fsutil.dao.annotation.NonSave;

public class GmNoticeInfo {
	private String title;   		//标题
	private String content;			//公告内容
	private long startTime;			//开始时间
	private long endTime;			//结束时间
	private int cycleInterval;		//循环间隔
	private int priority;			//优先级
	@NonSave
	private long lastBroadcastTime; //上次广播时间
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
	public int getCycleInterval() {
		return cycleInterval;
	}
	public void setCycleInterval(int cycleInterval) {
		this.cycleInterval = cycleInterval;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public long getLastBroadcastTime() {
		return lastBroadcastTime;
	}
	public void setLastBroadcastTime(long lastBroadcastTime) {
		this.lastBroadcastTime = lastBroadcastTime;
	}
}
