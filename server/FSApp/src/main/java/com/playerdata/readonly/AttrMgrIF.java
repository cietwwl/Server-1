package com.playerdata.readonly;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.RoleAttrData;

/*
 * @author HC
 * @date 2016年5月27日 下午5:08:11
 * @Description 属性的只读接口
 */
public interface AttrMgrIF {
	/**
	 * 获取属性
	 * 
	 * @return
	 */
	public RoleAttrData getRoleAttrData();
	
	/**
	 * 
	 * @return
	 */
	public AttrData getTotalAttrData();
}