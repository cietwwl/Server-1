package com.rw.service.dropitem;

import java.util.concurrent.ConcurrentHashMap;

public class DropGuaranteeData {

	private ConcurrentHashMap<Integer, DropGuaranteeRecord> store;

	public ConcurrentHashMap<Integer, DropGuaranteeRecord> getStore() {
		return store;
	}

	public void setStore(ConcurrentHashMap<Integer, DropGuaranteeRecord> store) {
		ConcurrentHashMap<Integer, DropGuaranteeRecord> temp = new ConcurrentHashMap<Integer, DropGuaranteeRecord>(store.size(), 1.0f, 1);
		temp.putAll(store);
		this.store = temp;
	}

	public void initStore() {
		this.store = new ConcurrentHashMap<Integer, DropGuaranteeRecord>(8, 1.0f, 1);
	}

	public DropGuaranteeRecord getRecord(Integer guaranteeId) {
		return this.store.get(guaranteeId);
	}

	public void addRecord(Integer guaranteeId, DropGuaranteeRecord record) {
		this.store.put(guaranteeId, record);
	}
}
