package com.rw.fsutil.dao.mapitem;

import java.util.Map;

public interface MapItemRowBuider<T> {

	public T builde(Object key, MapItemEntity entity);
	
	public T mapRow(Object key, Map<String, Object> rs);
}
