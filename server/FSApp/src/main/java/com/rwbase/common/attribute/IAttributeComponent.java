package com.rwbase.common.attribute;

/**
 * 属性组件
 * 
 * @author Jamaz
 *
 */
public interface IAttributeComponent {

	/**
	 * 转化成属性集
	 * 
	 * @param userId
	 * @return
	 */
	public AttributeSet convertToAttribute(String userId, String heroId);

	// /**
	// * 通过传递的参数计算属性
	// *
	// * @param obj
	// * @return
	// */
	// public AttributeSet calc(Object obj);

	/**
	 * 获取模块的枚举类型
	 * 
	 * @return
	 */
	public AttributeComponentEnum getComponentTypeEnum();
}