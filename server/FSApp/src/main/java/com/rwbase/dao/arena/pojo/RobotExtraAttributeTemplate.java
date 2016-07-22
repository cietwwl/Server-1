package com.rwbase.dao.arena.pojo;

import java.util.Map;

import com.rwbase.common.attribute.AttributeUtils;

/*
 * @author HC
 * @date 2016年7月15日 下午4:48:49
 * @Description 
 */
public class RobotExtraAttributeTemplate {
	private final int extraAttrId;// 额外属性的Id
	private final Map<Integer, Integer> attrDataMap;// 羁绊增加属性
	private final Map<Integer, Integer> precentAttrDataMap;// 羁绊增加的百分比属性

	public RobotExtraAttributeTemplate(RobotExtraAttributeCfg cfg) {
		this.extraAttrId = cfg.getExtraAttrId();
		// ===============================增加的固定属性
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("RobotExtraAttributeCfg", cfg.getAttrData());
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("RobotExtraAttributeCfg", cfg.getPrecentAttrData());
	}

	public int getExtraAttrId() {
		return extraAttrId;
	}

	public Map<Integer, Integer> getAttrDataMap() {
		return attrDataMap;
	}

	public Map<Integer, Integer> getPrecentAttrDataMap() {
		return precentAttrDataMap;
	}
}