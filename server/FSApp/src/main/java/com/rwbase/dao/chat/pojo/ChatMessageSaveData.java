package com.rwbase.dao.chat.pojo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.rw.fsutil.util.jackson.JsonUtil;

/**
 * @author HC
 * @date 2016年6月27日 下午4:58:36
 * @Description 
 */
@JsonDeserialize(using = ChatMessageSaveData.ChatMessageSaveDataDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown=true, value={"time"})
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
	
//	@JsonCreator
//	public static ChatMessageSaveData forValue(@JsonProperty("sendInfo") ChatUserInfo sendInfo, @JsonProperty("receiveInfo") ChatUserInfo receiveInfo, @JsonProperty("message") String message,
//			@JsonProperty("sendTime") long sendTime, @JsonProperty("secCfgId") int secCfgId, @JsonProperty("secId") String secId, @JsonProperty("read") boolean isRead,
//			@JsonProperty("inviteNum") int inviteNum, @JsonProperty("9") List<ChatAttachmentSaveData> pAttachment) {
//		ChatMessageSaveData cmsd = new ChatMessageSaveData();
//		cmsd.sendInfo = sendInfo;
//		cmsd.receiveInfo = receiveInfo;
//		cmsd.message = message;
//		cmsd.sendTime = sendTime;
//		cmsd.secCfgId = secCfgId;
//		cmsd.secId = secId;
//		cmsd.isRead = isRead;
//		cmsd.inviteNum = inviteNum;
//		if (pAttachment != null) {
//			cmsd._attachments = pAttachment;
//		}
//		return cmsd;
//	}
	
	public static class ChatMessageSaveDataDeserializer extends JsonDeserializer<ChatMessageSaveData> {

		private Class<?> getParameterTypeOfGenericField(Field f, boolean isMap) {
			Type type = f.getGenericType();
			Class<?> targetClass;
			if (type instanceof ParameterizedType) {
				ParameterizedType actualType = (ParameterizedType) type;
				targetClass = actualType.getActualTypeArguments()[isMap ? 1 : 0].getClass();
			} else {
				targetClass = Object.class;
			}
			return targetClass;
		}
		
		@Override
		public ChatMessageSaveData deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = jp.getCodec().readTree(jp);
			ChatMessageSaveData cmsd = new ChatMessageSaveData();
			Map<String, Field> fieldMap = new HashMap<String, Field>();
			if (node.has("sendInfo")) {
				if (node.has("sendInfo")) {
					cmsd.sendInfo = JsonUtil.readValue(node.get("sendInfo").toString(), ChatUserInfo.class);
				}
				if (node.has("receiveInfo")) {
					cmsd.receiveInfo = JsonUtil.readValue(node.get("receiveInfo").toString(), ChatUserInfo.class);
				}
				if (node.has("message")) {
					cmsd.message = node.get("message").asText();
				}
				if (node.has("sendTime")) {
					cmsd.sendTime = node.get("sendTime").asLong();
				}
				if (node.has("secCfgId")) {
					cmsd.secCfgId = node.get("secCfgId").asInt();
				}
				if (node.has("secId")) {
					cmsd.secId = node.get("secId").asText();
				}
				if (node.has("read")) {
					cmsd.read = node.get("read").asBoolean();
				}
				if (node.has("inviteNum")) {
					cmsd.inviteNum = node.get("inviteNum").asInt();
				}
			} else {
				try {
					if (node.has(_KEY_SENDER_INFO)) {
						cmsd.sendInfo = JsonUtil.readValue(node.get(_KEY_SENDER_INFO).toString(), ChatUserInfo.class);
					}
					if (node.has(_KEY_RECEIVER_INFO)) {
						cmsd.receiveInfo = JsonUtil.readValue(node.get(_KEY_RECEIVER_INFO).toString(), ChatUserInfo.class);
					}
					if (node.has(_KEY_MESSAGE)) {
						cmsd.message = node.get(_KEY_MESSAGE).asText();
					}
					if (node.has(_KEY_SEND_TIME)) {
						cmsd.sendTime = node.get(_KEY_SEND_TIME).asLong();
					}
					if (node.has(_KEY_SEC_CFG_ID)) {
						cmsd.secCfgId = node.get(_KEY_SEC_CFG_ID).asInt();
					}
					if (node.has(_KEY_SEC_ID)) {
						cmsd.secId = node.get(_KEY_SEC_ID).asText();
					}
					if (node.has(_KEY_IS_READ)) {
						cmsd.read = node.get(_KEY_IS_READ).asBoolean();
					}
					if (node.has(_KEY_INVITE_NUM)) {
						cmsd.inviteNum = node.get(_KEY_INVITE_NUM).asInt();
					}
					if(node.has(_KEY_ATTACHMENT)) {
						cmsd._attachments = JsonUtil.readList(node.get(_KEY_ATTACHMENT).toString(), ChatAttachmentSaveData.class);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (Iterator<Map.Entry<String, Field>> itr = fieldMap.entrySet().iterator(); itr.hasNext();) {
				Map.Entry<String, Field> entry = itr.next();
				Class<?> clazz = entry.getValue().getType();
				Object value;
				JsonNode currentNode = node.get(entry.getKey());
				if (clazz.isAssignableFrom(int.class)) {
					value = currentNode.asInt();
				} else if (clazz.isAssignableFrom(short.class)) {
					value = (short)currentNode.asInt();
				} else if (clazz.isAssignableFrom(byte.class)) {
					value = (byte)currentNode.asInt();
				} else if (clazz.isAssignableFrom(long.class)) {
					value = (short)currentNode.asInt();
				} else if (clazz.isAssignableFrom(char.class)) {
					value = (char)currentNode.asInt();
				} else if (clazz.isAssignableFrom(boolean.class)) {
					value = currentNode.asBoolean();
				} else if (clazz.isAssignableFrom(List.class)) {
					value = JsonUtil.readList(currentNode.toString(), getParameterTypeOfGenericField(entry.getValue(), false));
				} else if (clazz.isAssignableFrom(Map.class)) {
					value = JsonUtil.readList(currentNode.toString(), getParameterTypeOfGenericField(entry.getValue(), true));
				} else {
					value = JsonUtil.readValue(currentNode.toString(), clazz);
				}
				try {
					entry.getValue().set(cmsd, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return cmsd;
		}
		
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
	public void setSendInfo(ChatUserInfo sendInfo) {
		this.sendInfo = sendInfo;
	}

	@JsonIgnore
	public void setReceiveInfo(ChatUserInfo receiveInfo) {
		this.receiveInfo = receiveInfo;
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
				+ ", isRead=" + read + ", inviteNum=" + inviteNum + "]";
	}

	@Override
	public int compareTo(ChatMessageSaveData o) {
		return this.sendTime < o.sendTime ? -1 : 1;
	}
	
}