package com.rwbase.dao.email;

import java.util.List;

public class EmailData {	
	private String title = "";//标题
	private String content = "";//内容
	private String sender = "";//发件人
	private String checkIcon = "btn_YouJian_h";//已读图标
	private String subjectIcon = "btn_YouJian_n";//未读图标
	private String emailAttachment = "";//附件列表
	private EEmailDeleteType deleteType = EEmailDeleteType.DEADLINE_TIME;//邮件删除类型
	//延迟时间 按秒计算
	private int delayTime = 604800;//删除延时(默认7天)
	private String deadlineTime = "";//到期删除 格式：2015/6/9
	private long taskId;//任务id， 同一个任务的email只能有一封,避免重发，
	
	//冷冻时间
	private long  coolTime;
	//可领取奖励开始时间
	private long  beginTime;
	//可领取奖励结束时间
	private long  endTime;
	
	
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
	
	public void replaceContent(List<String> args){
		int index = 0;
		while(args.size() > index){
			String oldStr = "{S" + index + "}";
			if(content.indexOf(oldStr) != -1){//找到
				content = content.replace(oldStr, args.get(index));
			}
			index++;
		}
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getCheckIcon() {
		return checkIcon;
	}
	public void setCheckIcon(String checkIcon) {
		this.checkIcon = checkIcon;
	}
	public String getSubjectIcon() {
		return subjectIcon;
	}
	public void setSubjectIcon(String subjectIcon) {
		this.subjectIcon = subjectIcon;
	}
	public String getEmailAttachment() {
		return emailAttachment;
	}
	public void setEmailAttachment(String emailAttachment) {
		this.emailAttachment = emailAttachment;
	}
	public EEmailDeleteType getDeleteType() {
		return deleteType;
	}
	public void setDeleteType(EEmailDeleteType deleteType) {
		this.deleteType = deleteType;
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
	
	public boolean isReceiveDelete(){
		return deleteType == EEmailDeleteType.GET_DELETE;
	}
	
	public boolean isDelay(){
		return deleteType == EEmailDeleteType.DELAY_TIME;
	}
	
	public boolean isDeadline(){
		return deleteType == EEmailDeleteType.DEADLINE_TIME;
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
