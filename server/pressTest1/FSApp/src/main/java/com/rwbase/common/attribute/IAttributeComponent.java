package com.rwbase.common.attribute;

/**
 * 属性组件
 * @author Jamaz
 *
 */
public interface IAttributeComponent {

	/**
	 * 转化成属性集
	 * @param userId
	 * @return
	 */
	public AttributeSet convertToAttribute(String userId,String heroId);
	
}
