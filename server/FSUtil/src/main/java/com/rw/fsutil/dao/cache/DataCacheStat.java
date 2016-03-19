package com.rw.fsutil.dao.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataCacheStat {

	private  List<DataCache> list;

	public DataCacheStat() {
		this.list = new ArrayList<DataCache>();
	}

	public synchronized void register(DataCache cache) {
		this.list.add(cache);
	}
	
	
}
