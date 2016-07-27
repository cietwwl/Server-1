package com.rwbase.dao.chat.pojo;

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author HC
 * @date 2016年6月27日 下午4:58:36
 * @Description 
 */
@JsonIgnoreProperties(value={"time"})
public class ChatMessageSaveData implements Comparable<ChatMessageSaveData> {
	
	private static final String _KEY_SENDER_INFO = "1";
	private static final String _KEY_RECEIVER_INFO = "2";
	private static final String _KEY_MESSAGE = "3";
	private static final String _KEY_SEND_TIME = "4";
	private static final String _KEY_SEC_CFG_ID = "5";
	private static final String _KEY_SEC_ID = "6";
	private static final String _KEY_IS_READ = "7";
	private static final String _KEY_INVITE_NUM = "8";
	private static final String _KEY_ATTACHMENT = "9";
	
	@JsonCreator
	public static ChatMessageSaveData forValue(@JsonProperty("sendInfo") ChatUserInfo sendInfo, @JsonProperty("receiveInfo") ChatUserInfo receiveInfo, @JsonProperty("message") String message,
			@JsonProperty("sendTime") long sendTime, @JsonProperty("secCfgId") int secCfgId, @JsonProperty("secId") String secId, @JsonProperty("read") boolean isRead,
			@JsonProperty("inviteNum") int inviteNum, @JsonProperty("9") List<ChatAttachmentSaveData> pAttachment) {
		ChatMessageSaveData cmsd = new ChatMessageSaveData();
		cmsd.sendInfo = sendInfo;
		cmsd.receiveInfo = receiveInfo;
		cmsd.message = message;
		cmsd.sendTime = sendTime;
		cmsd.secCfgId = secCfgId;
		cmsd.secId = secId;
		cmsd.isRead = isRead;
		cmsd.inviteNum = inviteNum;
		if (pAttachment != null) {
			cmsd._attachments = pAttachment;
		}
		return cmsd;
	}
	
	@JsonSerialize(include=Inclusion.NON_NULL)
	@JsonProperty(_KEY_SENDER_INFO)
	private ChatUserInfo sendInfo; // 发送者信息
	
	@JsonSerialize(include=Inclusion.NON_NULL)
	@JsonProperty(_KEY_RECEIVER_INFO)
	private ChatUserInfo receiveInfo; // 接收者信息
	
	@JsonProperty(_KEY_MESSAGE)
	private String message;// 消息
	
	@JsonProperty(_KEY_SEND_TIME)
	private long sendTime; // 时间（改为long）
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_SEC_CFG_ID)
	private int secCfgId;// 秘境的模版Id
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_SEC_ID)
	private String secId;// 秘境的Id
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_IS_READ)
	private boolean isRead;// 是否已经读了
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_INVITE_NUM)
	private int inviteNum;// 邀请的人数
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_ATTACHMENT)
	private List<ChatAttachmentSaveData> _attachments = Collections.emptyList(); // 附件列表
	
	private List<ChatAttachmentSaveData> _attachmentsRO;
	
	public void setSendInfo(ChatUserInfo sendInfo) {
		this.sendInfo = sendInfo;
	}

	public void setReceiveInfo(ChatUserInfo receiveInfo) {
		this.receiveInfo = receiveInfo;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
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
	
	public void setAttachment(List<ChatAttachmentSaveData> attachments) {
		this._attachments = attachments;
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
	
	public List<ChatAttachmentSaveData> getAttachments() {
		if (_attachments.equals(Collections.EMPTY_LIST)) {
			return _attachments;
		}
		if (_attachmentsRO == null) {
			_attachmentsRO = Collections.unmodifiableList(_attachments);
		}
		return _attachmentsRO;
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