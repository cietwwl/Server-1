package com.rw.service.guide.datamodel;

import com.common.BaseConfig;

public class GiveItemCfg extends BaseConfig{
	private int key; // key
	private String modleId; // 物品ID（不能为空）
	private int count; // 赠送数量
	private int autoSentLevel = -1;//达到这个等级就自动送，如果为-1或者0就需要客户端触发

	public int getKey() {
		return key;
	}

	public String getModleId() {
		return modleId;
	}

	public int getCount() {
		return count;
	}

	public int getAutoSentLevel() {
		return autoSentLevel;
	}
	
}
