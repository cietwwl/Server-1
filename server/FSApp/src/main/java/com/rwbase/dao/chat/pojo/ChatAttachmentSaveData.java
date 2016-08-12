package com.rwbase.dao.chat.pojo;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.rwproto.ChatServiceProtos.ChatAttachItem;

public class ChatAttachmentSaveData {

	private static final String _KEY_TYPE = "1";
	private static final String _KEY_ID = "2";
	private static final String _EXTRA_INFO = "3";

	@JsonProperty(_KEY_TYPE)
	private int _type; // 附件的类型

	@JsonProperty(_KEY_ID)
	private String _id; // 附件的id（如果是表情的话，就是表情的模板id）

	@JsonProperty(_EXTRA_INFO)
	@JsonSerialize(include = Inclusion.NON_DEFAULT)
	private String _extraInfo;// 附件额外的信息
	
	public void translate(ChatAttachItem protoData) {
		this._type = protoData.getType();
		this._id = protoData.getId();
		if (protoData.hasExtraInfo()) {
			this._extraInfo = protoData.getExtraInfo();
		}
	}

	public ChatAttachItem toProto() {
		ChatAttachItem.Builder builder = ChatAttachItem.newBuilder();
		builder.setType(this._type);
		builder.setId(this._id);
		if (this._extraInfo != null) {
			builder.setExtraInfo(this._extraInfo);
		}
		return builder.build();
	}

	@Override
	public String toString() {
		return "ChatAttachmentSaveData [_type=" + _type + ", _id=" + _id + ", _extraInfo=" + _extraInfo + "]";
	}
}
