package com.rwbase.dao.email;


public class EmailCfg {
	private int id;
	private String title;
	private int deleteType;
	private int delayTime;
	private String deadlineTime;
	private String sender;
	private String subjectIcon;
	private String checkIcon;
	private String content;
	private String attachment;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	public String getDeadlineTime() {
		return deadlineTime;
	}
	public void setDeadlineTime(String deadlineTime) {
		this.deadlineTime = deadlineTime;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSubjectIcon() {
		return subjectIcon;
	}
	public void setSubjectIcon(String subjectIcon) {
		this.subjectIcon = subjectIcon;
	}
	public String getCheckIcon() {
		return checkIcon;
	}
	public void setCheckIcon(String checkIcon) {
		this.checkIcon = checkIcon;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	public int getDeleteType() {
		return deleteType;
	}
	public void setDeleteType(int deleteType) {
		this.deleteType = deleteType;
	}
}
