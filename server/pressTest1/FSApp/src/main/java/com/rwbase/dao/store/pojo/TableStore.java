package com.rwbase.dao.store.pojo;

import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_store")
public class TableStore {
	@Id
	private String userId;
	private ConcurrentHashMap<Integer, StoreData> storeDataMap = new ConcurrentHashMap<Integer, StoreData>();
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public ConcurrentHashMap<Integer, StoreData> getStoreDataMap() {
		return storeDataMap;
	}
	public void setStoreDataMap(ConcurrentHashMap<Integer, StoreData> storeVo) {
		this.storeDataMap = storeVo;
	}

}
