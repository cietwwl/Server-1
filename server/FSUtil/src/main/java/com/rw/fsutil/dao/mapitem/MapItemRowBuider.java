package com.rw.fsutil.dao.mapitem;

public interface MapItemRowBuider<T> {

	public T builde(String key, MapItemEntity entity);
}
