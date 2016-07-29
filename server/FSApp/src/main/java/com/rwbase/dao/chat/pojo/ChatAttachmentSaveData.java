package com.rwbase.dao.chat.pojo;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.rwproto.ChatServiceProtos.ChatAttachItem;

public class ChatAttachmentSaveData {

	private static final String _KEY_TYPE = "1";
	private static final String _KEY_ID = "2";
	private static final String _KEY_INDEX = "3";
	private static final String _KEY_LEVEL = "4";
	private static final String _KEY_QUALITY = "5";
	private static final String _KEY_STAR = "6";
	
	@JsonProperty(_KEY_TYPE)
	private int _type; // 附件的类型

	@JsonProperty(_KEY_ID)
	private String _id; // 附件的id（如果是表情的话，就是表情的模板id）
	
	@JsonProperty(_KEY_INDEX)
	private int _index; // 附件在聊天中的索引
	
	@JsonProperty(_KEY_LEVEL)
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	private int _level; // 附件的等级
	
	@JsonProperty(_KEY_QUALITY)
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	private String _qualityId; // 附件的品质
	
	@JsonProperty(_KEY_STAR)
	@JsonSerialize(include=Inclusion.NON_DEFAULT)
	private String _star; // 附件的星级
	
	public void translate(ChatAttachItem protoData) {
		this._type = protoData.getType();
		this._id = protoData.getId();
		this._index = protoData.getIndex();
		if (protoData.hasLevel()) {
			this._level = protoData.getLevel();
		}
		if (protoData.hasQualityId()) {
			this._qualityId = protoData.getQualityId();
		}
		if (protoData.hasStar()) {
			this._star = protoData.getStar();
		}
	}
	
	public ChatAttachItem toProto() {
		ChatAttachItem.Builder builder = ChatAttachItem.newBuilder();
		builder.setType(this._type);
		builder.setId(this._id);
		builder.setIndex(this._index);
		if (_level > 0) {
			builder.setLevel(this._level);
		}
		if (_qualityId != null) {
			builder.setQualityId(this._qualityId);
		}
		if (_star != null) {
			builder.setStar(this._star);
		}
		return builder.build();
	}
	
	@Override
	public String toString() {
		return "ChatAttachmentSaveData [type=" + _type + ", id=" + _id + ", index=" + _index + ", level=" + _level + ", qualityId=" + _qualityId + ", star=" + _star + "]";
	}
}
