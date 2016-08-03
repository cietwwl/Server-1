package com.rwbase.dao.chat.pojo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author HC
 * @date 2016年6月27日 下午4:58:36
 * @Description 
 */
@JsonDeserialize(using = ChatMessageSaveData.ChatMessageSaveDataDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown=true, value={"time"})
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
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
	
	private static final Map<String, Field> _oldKeyMapping = new HashMap<String, Field>(); // 舊數據每個field對應的名字
	private static final Map<String, Field> _newKeyMapping = new HashMap<String, Field>(); // 新數據每個field對應的名字
	private static final Map<String, Field> _userInfoFieldMapping = new HashMap<String, Field>(); // 舊數據中兩個userInfo的field
	
	static {
		Field[] allFields = ChatMessageSaveData.class.getDeclaredFields();
		for(int i = 0; i < allFields.length; i++) {
			Field f = allFields[i];
			JsonProperty jp = f.getAnnotation(JsonProperty.class);
			if(jp != null) {
				f.setAccessible(true);
				_oldKeyMapping.put(f.getName(), f); // 舊數據是用字段名作為key的
				_newKeyMapping.put(jp.value(), f); // 新數據是用自定義key的
			}
		}
		try {
			Field fSendInfo = ChatMessageSaveData.class.getDeclaredField("sendInfo");
			Field fReceiveInfo = ChatMessageSaveData.class.getDeclaredField("receiveInfo");
			fSendInfo.setAccessible(true);
			fReceiveInfo.setAccessible(true);
			_userInfoFieldMapping.put(_KEY_SENDER_INFO, fSendInfo);
			_userInfoFieldMapping.put(_KEY_RECEIVER_INFO, fReceiveInfo);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static class ChatMessageSaveDataDeserializer extends JsonDeserializer<ChatMessageSaveData> {
		
		@Override
		public ChatMessageSaveData deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			ChatMessageSaveData cmsd = new ChatMessageSaveData();
			Map<String, Field> fieldMap = new HashMap<String, Field>();
			if (node.has(_KEY_SENDER_INFO) || node.has(_KEY_RECEIVER_INFO)) {
				// 新的數據保存方式
				fieldMap = _newKeyMapping;
			} else {
				// 舊數據兼容
				fieldMap = _oldKeyMapping;
			}
			for (Iterator<Map.Entry<String, Field>> itr = fieldMap.entrySet().iterator(); itr.hasNext();) {
				Map.Entry<String, Field> entry = itr.next();
				JsonNode currentNode = node.get(entry.getKey());
				if (currentNode == null) {
					continue;
				}
				String key = entry.getKey();
				if (key.equals(_KEY_SENDER_INFO) || key.equals(_KEY_RECEIVER_INFO)) {
					/* 2016-08-01 由於_KEY_SENDER_INFO和_KEY_RECEIVER_INFO原來是保存成為一個ChatUserInfo，新版本數據調整之後，
					   改為只保存一個userId，所以這裡要做新舊數據的兼容。
					 */
					if (currentNode.isObject()) {
						// 曾經這兩個key是直接保存ChatUserInfo
						Field tempField = _userInfoFieldMapping.get(key);
						CommonJsonFieldValueSetter.setValue(currentNode, tempField, cmsd);
						try {
							ChatUserInfo cui = (ChatUserInfo) tempField.get(cmsd);
							entry.getValue().set(cmsd, cui.getUserId()); // 賦值相應的userId
						} catch (Exception e) {
							e.printStackTrace();
						}
						continue;
					}
				}
				CommonJsonFieldValueSetter.setValue(currentNode, entry.getValue(), cmsd);
			}
			return cmsd;
		}
		
	}
	
	@JsonSerialize(include=Inclusion.NON_NULL)
	@JsonProperty(_KEY_SENDER_INFO)
	private String _senderUserId; // sender的userId
	
	@JsonSerialize(include=Inclusion.NON_NULL)
	@JsonProperty(_KEY_RECEIVER_INFO)
	private String _receiverUserId; // receiver的userId
	
//	@JsonSerialize(include=Inclusion.NON_NULL)
//	@JsonProperty(_KEY_SENDER_INFO)
	private ChatUserInfo sendInfo; // 发送者信息
	
//	@JsonSerialize(include=Inclusion.NON_NULL)
//	@JsonProperty(_KEY_RECEIVER_INFO)
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
	private boolean read;// 是否已经读了
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_INVITE_NUM)
	private int inviteNum;// 邀请的人数
	
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	@JsonProperty(_KEY_ATTACHMENT)
	private List<ChatAttachmentSaveData> _attachments = Collections.emptyList(); // 附件列表
	
	@JsonIgnore
	private List<ChatAttachmentSaveData> _attachmentsRO;
	
	@JsonIgnore
	public void setSenderUserId(String pUserId) {
		this._senderUserId = pUserId;
	}
	
	@JsonIgnore
	public void sendReceiverUserId(String pUserId) {
		this._receiverUserId = pUserId;
	}
	
	@JsonIgnore
	public void setSendInfo(ChatUserInfo sendInfo) {
		this.sendInfo = sendInfo;
		if(this._senderUserId == null) {
			this._senderUserId = this.sendInfo.getUserId();
		}
	}

	@JsonIgnore
	public void setReceiveInfo(ChatUserInfo receiveInfo) {
		this.receiveInfo = receiveInfo;
		if(this._receiverUserId == null) {
			this._receiverUserId = this.receiveInfo.getUserId();
		}
	}

	@JsonIgnore
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonIgnore
	public void setSendTime(long pTime) {
		this.sendTime = pTime;
	}

	@JsonIgnore
	public void setSecCfgId(int secCfgId) {
		this.secCfgId = secCfgId;
	}

	@JsonIgnore
	public void setSecId(String secId) {
		this.secId = secId;
	}
	
	@JsonIgnore
	public void setRead(boolean isRead) {
		this.read = isRead;
	}

	@JsonIgnore
	public void setInviteNum(int inviteNum) {
		this.inviteNum = inviteNum;
	}
	
	@JsonIgnore
	public void setAttachment(List<ChatAttachmentSaveData> attachments) {
		this._attachments = attachments;
	}
	
	@JsonIgnore
	public String getSenderUserId() {
		return _senderUserId;
	}
	
	@JsonIgnore
	public String getReceiverUserId() {
		return _receiverUserId;
	}

	@JsonIgnore
	public ChatUserInfo getSendInfo() {
		return sendInfo;
	}

	@JsonIgnore
	public ChatUserInfo getReceiveInfo() {
		return receiveInfo;
	}

	@JsonIgnore
	public String getMessage() {
		return message;
	}
	
	@JsonIgnore
	public long getSendTime() {
		return sendTime;
	}

	@JsonIgnore
	public int getSecCfgId() {
		return secCfgId;
	}

	@JsonIgnore
	public String getSecId() {
		return secId;
	}

	@JsonIgnore
	public boolean isRead() {
		return read;
	}

	@JsonIgnore
	public int getInviteNum() {
		return inviteNum;
	}
	
	@JsonIgnore
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
				+ ", isRead=" + read + ", inviteNum=" + inviteNum + ", attachments=" + _attachments + "]";
	}

	@Override
	public int compareTo(ChatMessageSaveData o) {
		return this.sendTime < o.sendTime ? -1 : 1;
	}
	
}