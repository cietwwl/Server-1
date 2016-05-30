package com.rwbase.dao.upgrade;

public class VersionUpdateCfg {
	private String versionNo;
	private String forceUpdateTime;
	private String updateTips;
	private String forceTips;
	private String rewards;
	private String emailTitle;
	private String emailContent;
	private String sender;
	
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getForceUpdateTime() {
		return forceUpdateTime;
	}
	public void setForceUpdateTime(String forceUpdateTime) {
		this.forceUpdateTime = forceUpdateTime;
	}
	public String getUpdateTips() {
		return updateTips;
	}
	public void setUpdateTips(String updateTips) {
		this.updateTips = updateTips;
	}
	public String getForceTips() {
		return forceTips;
	}
	public void setForceTips(String forceTips) {
		this.forceTips = forceTips;
	}
	public String getRewards() {
		return rewards;
	}
	public void setRewards(String rewards) {
		this.rewards = rewards;
	}
	public String getEmailTitle() {
		return emailTitle;
	}
	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}
	public String getEmailContent() {
		return emailContent;
	}
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	
}
