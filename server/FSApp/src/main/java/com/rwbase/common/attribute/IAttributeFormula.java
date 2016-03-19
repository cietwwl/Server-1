package com.rwbase.common.attribute;

import java.util.List;
import java.util.Map;

public interface IAttributeFormula<T> {

	/**
	 * <pre>
	 * 定义需要二次计算属性类型，不是简单的把属性累加起来
	 * 比如：攻击力 = 力量 * 1.5 + 敏捷 * 0.4
	 * </pre>
	 * 
	 * @return
	 */
	public List<AttributeType> getReCalculateAttributes();

	/**
	 * 二次计算指定属性
	 * 
	 * @param extracter
	 * @param type
	 * @return
	 */
	public int recalculate(IAttributeExtracter extracter, AttributeType type);
	
	/**
	 * 转换成指定对象
	 * @param result
	 * @return
	 */
	public T convert(Map<AttributeType, Integer> result);
}
