package com.rwbase.common.attrdata;

/*
 * @author HC
 * @date 2016年4月29日 上午10:03:32
 * @Description 
 */
public enum AttrDataType {
	ATTR_DATA_TYPE(0), // 固定值类型属性
	ATTR_DATA_PRECENT_TYPE(1);// 百分比类型属性

	public final int type;

	private AttrDataType(int type) {
		this.type = type;
	}
}