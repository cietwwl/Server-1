package com.playerdata.fixEquip.exp.cfg;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.fixEquip.FixEquipCostType;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;


public class FixExpEquipQualityCfg {

	private String id;

	private String planId;	
	
	private int quality;	

	private int levelNeed;
	
	private FixEquipCostType costType;
	
	private int costCount;	
	
	//modelAId:count;modelBId:count
	private String itemsNeedStr;
	
	private Map<Integer,Integer> itemsNeed = new HashMap<Integer,Integer>();
	
	private String attrData;
	
	private String precentAttrData;
	
	
	public String getId() {
		return id;
	}

	public int getQuality() {
		return quality;
	}

	public int getLevelNeed() {
		return levelNeed;
	}

	public FixEquipCostType getCostType() {
		return costType;
	}

	public int getCostCount() {
		return costCount;
	}

	public String getItemsNeedStr() {
		return itemsNeedStr;
	}

	public Map<Integer, Integer> getItemsNeed() {
		return itemsNeed;
	}

	public void setItemsNeed(Map<Integer, Integer> itemsNeed) {
		this.itemsNeed = itemsNeed;
	}

	public String getPlanId() {
		return planId;
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
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("FixExpEquipQualityCfg", attrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("FixExpEquipQualityCfg", precentAttrData);
	}
	
	
	
	
}
