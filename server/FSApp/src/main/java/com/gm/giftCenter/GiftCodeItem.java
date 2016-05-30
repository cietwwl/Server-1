package com.gm.giftCenter;

import java.util.HashMap;
import java.util.Map;

import com.gm.gmsender.GmCallBack;

public class GiftCodeItem {

	private final String activateCode;

	private final String userId;

	private String id;

	private long iSequenceNum;

	private final int channelId;

	private GmCallBack<GiftCodeResponse> gmCallBack;

	public GiftCodeItem(String code, String userId, int channelId, GmCallBack<GiftCodeResponse> gmCallBack) {
		this.activateCode = code;
		this.userId = userId;
		this.gmCallBack = gmCallBack;
		this.channelId = channelId;
		this.id = this.userId + "_" + this.activateCode;
	}

	public String getId() {
		return this.id;
	}

	public String getUserId() {
		return userId;
	}

	public Map<String, Object> toGmSendItemData() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("opType", 20039);
		args.put("activateCode", this.activateCode);
		args.put("iSequenceNum", this.iSequenceNum);
		args.put("roleId", this.userId);
		args.put("channel", this.channelId);

		return args;
	}

	public GmCallBack<GiftCodeResponse> getGmCallBack() {
		return gmCallBack;
	}
}