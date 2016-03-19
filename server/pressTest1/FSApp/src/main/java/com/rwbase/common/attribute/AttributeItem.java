package com.rwbase.common.attribute;

/**
 * 属性项
 * 
 * @author Jamaz
 *
 */
public class AttributeItem {

	private final AttributeType type; // 属性类型
	private final int increaseValue; // 增加的值
	private final int incPerTenthousand; // 增加的千分比

	public AttributeItem(AttributeType type, int increaseValue, int incPerTenthousand) {
		this.type = type;
		this.increaseValue = increaseValue;
		this.incPerTenthousand = incPerTenthousand;
	}

	public int getIncreaseValue() {
		return increaseValue;
	}

	public int getIncPerTenthousand() {
		return incPerTenthousand;
	}

	public AttributeType getType() {
		return type;
	}

}
