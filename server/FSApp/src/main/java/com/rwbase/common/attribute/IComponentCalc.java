package com.rwbase.common.attribute;

/*
 * @author HC
 * @date 2016年5月14日 下午7:51:51
 * @Description 
 */
public interface IComponentCalc {
	/**
	 * 通过传递的参数计算属性
	 * 
	 * @param obj
	 * @return
	 */
	public AttributeSet calc(Object obj);

	/**
	 * 获取模块的枚举类型
	 * 
	 * @return
	 */
	public AttributeComponentEnum getComponentTypeEnum();
}