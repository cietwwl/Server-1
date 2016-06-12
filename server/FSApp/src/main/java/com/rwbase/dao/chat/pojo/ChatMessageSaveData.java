package com.rwbase.dao.chat.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2016年6月27日 下午4:58:36
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageSaveData {
	private ChatUserInfo sendInfo;
	private ChatUserInfo receiveInfo;
	private String message;// 消息
	private String time;// 时间
	private int secCfgId;// 秘境的模版Id
	private String secId;// 秘境的Id
	private boolean isRead;// 是否已经读了
	private int inviteNum;// 邀请的人数

	public void setSendInfo(ChatUserInfo sendInfo) {
		this.sendInfo = sendInfo;
	}

	public void setReceiveInfo(ChatUserInfo receiveInfo) {
		this.receiveInfo = receiveInfo;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setSecCfgId(int secCfgId) {
		this.secCfgId = secCfgId;
	}

	public void setSecId(String secId) {
		this.secId = secId;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public void setInviteNum(int inviteNum) {
		this.inviteNum = inviteNum;
	}

	public ChatUserInfo getSendInfo() {
		return sendInfo;
	}

	public ChatUserInfo getReceiveInfo() {
		return receiveInfo;
	}

	public String getMessage() {
		return message;
	}

	public String getTime() {
		return time;
	}

	public int getSecCfgId() {
		return secCfgId;
	}

	public String getSecId() {
		return secId;
	}

	public boolean isRead() {
		return isRead;
	}

	public int getInviteNum() {
		return inviteNum;
	}
}