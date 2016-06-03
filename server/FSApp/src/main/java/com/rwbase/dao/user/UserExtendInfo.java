package com.rwbase.dao.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/***
 * 玩家扩展信息
 * @author Allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserExtendInfo {

	//封号原因
	private String blockReason;
	//封号解除时间，-1永久封号
	private long blockCoolTime = 0;
	
	//禁言原因
	private String chatBanReason;
	//禁言解除时间，-1永久禁言
	private long chatBanCoolTime = 0;
	
	//已回复未阅读的反馈
	private int feedbackId = 0;
	
	public long getBlockCoolTime() {
		return blockCoolTime;
	}
	public void setBlockCoolTime(long blockCoolTime) {
		this.blockCoolTime = blockCoolTime;
	}
	public String getBlockReason() {
		return blockReason;
	}
	public void setBlockReason(String blockReason) {
		this.blockReason = blockReason;
	}
	public String getChatBanReason() {
		return chatBanReason;
	}
	public void setChatBanReason(String chatBanReason) {
		this.chatBanReason = chatBanReason;
	}
	public long getChatBanCoolTime() {
		return chatBanCoolTime;
	}
	public void setChatBanCoolTime(long chatBanCoolTime) {
		this.chatBanCoolTime = chatBanCoolTime;
	}
	public int getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}
	
	
	

}
