package com.playerdata.readonly;

import java.util.Enumeration;

import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.store.pojo.StoreDataIF;


public interface StoreMgrIF {
	public Enumeration<? extends StoreDataIF> getStoreEnumeration();
	public StoreDataIF getStore(eStoreType type);
	
}