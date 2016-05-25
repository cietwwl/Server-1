package com.playerdata.fixEquip.norm.cfg;

import java.util.Map;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;



public class FixNormEquipLevelCfg {

	private String id;
	//所属活动配置id
	private String planId;	
	
	private int level;
	
	
	private String attrData;
	
	private String precentAttrData;

	public String getId() {
		return id;
	}	

	public String getPlanId() {
		return planId;
	}

	public int getLevel() {
		return level;
	}

	public String getAttrData() {
		return attrData;
	}

	public String getPrecentAttrData() {
		return precentAttrData;
	}



	private Map<Integer, Integer> precentAttrDataMap = null;
	
	private Map<Integer, Integer> attrDataMap = null;
	
	/**
	 * <pre>
	 * 获取增加的固定值属性
	 * 返回的这个Map的key是{@link AttributeType}的属性类型
	 * 返回的value（都是放大到了{@link AttributeConst#DIVISION}的倍数）有特殊处理，计算属性全部是用的int类型，然而为了防止
	 * 配置中会出现float类型的数据，所有这里凡是遇到在{@link AttrData}
	 * 中字段是float类型的的属性，都会把配置中的值扩大{@link AttributeConst#BIG_FLOAT}
	 * 的倍数
	 * </pre>
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getAttrDataMap() {
		return attrDataMap;
	}

	/**
	 * <pre>
	 * 获取增加的百分比属性
	 * 返回的这个Map的key是{@link AttributeType}的属性类型
	 * 返回的value（都是放大到了{@link AttributeConst#DIVISION}的倍数）有特殊处理，计算属性全部是用的int类型，然而为了防止
	 * 配置中会出现float类型的数据，所有这里凡是遇到在{@link AttrData}
	 * 中字段是float类型的的属性，都会把配置中的值扩大{@link AttributeConst#BIG_FLOAT}
	 * 的倍数
	 * </pre>
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getPrecentAttrDataMap() {
		return precentAttrDataMap;
	}

	/**
	 * 初始化解析属性
	 */
	public void initData() {
		// ===============================增加的固定属性
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("FixNormEquipLevelCfg", attrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("FixNormEquipLevelCfg", precentAttrData);
	}
	

	
	
	
	
}
