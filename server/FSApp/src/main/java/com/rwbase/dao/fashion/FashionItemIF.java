package com.rwbase.dao.fashion;

import java.util.concurrent.ConcurrentHashMap;

import com.rwbase.common.enu.eAttrIdDef;

public interface FashionItemIF {

	public String getId();

	public String getUserId();

	public int getType();

	public int getState();

	public long getBuyTime();
	
//	public ConcurrentHashMap<eAttrIdDef, Double> getAddPercentAttr();

}