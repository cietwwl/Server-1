package com.gm.task;

import java.util.List;

public class GmEmailRepItem {

	private String id;
	private String title;
	private String content;
	private List<GmItem> toolList; 
	private long sendTime;
//	private long receiveTime;
	private long expireTime;
	private long taskId;
	private long coolTime;
	private long beginTime;
	private long endTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public List<GmItem> getToolList() {
		return toolList;
	}
	public void setToolList(List<GmItem> toolList) {
		this.toolList = toolList;
	}
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
//	public long getReceiveTime() {
//		return receiveTime;
//	}
//	public void setReceiveTime(long receiveTime) {
//		this.receiveTime = receiveTime;
//	}
	public long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public long getCoolTime() {
		return coolTime;
	}
	public void setCoolTime(long coolTime) {
		this.coolTime = coolTime;
	}
	public long getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	
	
	
}
