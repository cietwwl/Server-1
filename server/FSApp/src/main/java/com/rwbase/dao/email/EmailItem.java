package com.rwbase.dao.email;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class EmailItem implements Cloneable {
	private String emailId = null; 	//邮件ID
	private boolean checked = false;//是否已读
	private boolean receive = false;//是否已领取
	private String emailAttachment;//附件列表
	private int deleteType;//删除类型  1.领取删除;2.延时删除;3.到期删除
	//过期时间
	private long deadlineTimeInMill;//删除延时
	private long sendTime;//发送时间
	private String title;//标题
	private String content;//内容
	private String sender;//发件人
	private String checkIcon;//已读图标
	private String subjectIcon;//未读图标
	private long taskId;//任务id， 同一个任务的email只能有一封,避免重发，
	
	//冷冻时间
	private long  coolTime;
	//可领取奖励开始时间
	private long  beginTime;
	//可领取奖励结束时间
	private long  endTime;
	//邮件编号，用来核实邮件类型 
	private String cfgid;
	public String getCfgid() {
		return cfgid;
	}
	public void setCfgid(String cfgid) {
		this.cfgid = cfgid;
	}
	public String getEmailAttachment() {
		return emailAttachment;
	}
	public void setEmailAttachment(String emailAttachment) {
		this.emailAttachment = emailAttachment;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public long getDeadlineTimeInMill() {
		return deadlineTimeInMill;
	}
	public void setDeadlineTimeInMill(long deadlineTimeInMill) {
		this.deadlineTimeInMill = deadlineTimeInMill;
	}
	public int getDeleteType() {
		return deleteType;
	}
	
	/**删除类型  1.领取删除;2.延时删除;3.到期删除*/
	public void setDeleteType(int deleteType) {
		this.deleteType = deleteType;
	}
	public boolean isReceive() {
		return receive;
	}
	public void setReceive(boolean receive) {
		this.receive = receive;
	}
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
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
