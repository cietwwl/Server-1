package com.rwbase.dao.chat.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/*
 * @author HC
 * @date 2016年6月27日 下午4:58:36
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true, value={"time"})
public class ChatMessageSaveData implements Comparable<ChatMessageSaveData> {
	@JsonSerialize(include=Inclusion.NON_NULL)
	private ChatUserInfo sendInfo;
	@JsonSerialize(include=Inclusion.NON_NULL)
	private ChatUserInfo receiveInfo;
	private String message;// 消息
//	private String time;// 时间
	private long sendTime; // 时间（改为long）
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

//	public void setTime(String time) {
//		this.time = time;
//	}
	
	public void setSendTime(long pTime) {
		this.sendTime = pTime;
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

//	public String getTime() {
//		return time;
//	}
	
	public long getSendTime() {
		return sendTime;
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

	@Override
	public String toString() {
		return "ChatMessageSaveData [sendInfo=" + sendInfo + ", receiveInfo=" + receiveInfo + ", message=" + message + ", sendTime=" + sendTime + ", secCfgId=" + secCfgId + ", secId=" + secId
				+ ", isRead=" + isRead + ", inviteNum=" + inviteNum + "]";
	}

	@Override
	public int compareTo(ChatMessageSaveData o) {
		return this.sendTime < o.sendTime ? -1 : 1;
	}
	
}