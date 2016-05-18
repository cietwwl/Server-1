package com.gm.customer.response;

import com.gm.customer.QuestionReply;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class QueryListResponse {
	private int id;
	private int channel;
	private int type;
	private int serverId;
	private String roleId;
	private String account;
	private String roleName;
	private long feedbackTime;
	private String feedbackContent;
	private String phone;
	private String model;
	private QuestionReply reply;
	private long iSequenceNum;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public long getFeedbackTime() {
		return feedbackTime;
	}
	public void setFeedbackTime(long feedbackTime) {
		this.feedbackTime = feedbackTime;
	}
	public String getFeedbackContent() {
		return feedbackContent;
	}
	public void setFeedbackContent(String feedbackContent) {
		this.feedbackContent = feedbackContent;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public QuestionReply getReply() {
		return reply;
	}
	public void setReply(QuestionReply reply) {
		this.reply = reply;
	}
	public long getiSequenceNum() {
		return iSequenceNum;
	}
	public void setiSequenceNum(long iSequenceNum) {
		this.iSequenceNum = iSequenceNum;
	}
}
