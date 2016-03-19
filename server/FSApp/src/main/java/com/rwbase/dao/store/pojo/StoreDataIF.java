package com.rwbase.dao.store.pojo;

import java.util.List;

import com.rwbase.common.enu.eStoreExistType;
import com.rwbase.common.enu.eStoreType;

public interface StoreDataIF {
	public List<CommodityData> getCommodity();
	public int getRefreshNum();
	public long getLastRefreshTime();
	public eStoreExistType getExistType();
	public eStoreType getType();
	public int getVersion();
}
