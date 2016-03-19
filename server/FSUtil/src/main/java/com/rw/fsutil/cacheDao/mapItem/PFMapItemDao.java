package com.rw.fsutil.cacheDao.mapItem;


public class PFMapItemDao<T> extends MapItemDao<T>{

	public PFMapItemDao(Class<T> clazzP) {
		super(clazzP,"dataSourcePF");
		// TODO Auto-generated constructor stub
	}
}
