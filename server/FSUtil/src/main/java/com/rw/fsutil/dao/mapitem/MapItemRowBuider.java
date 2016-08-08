package com.rw.fsutil.dao.mapitem;

import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

public interface MapItemRowBuider<T> extends RowMapper<T>{

	public T builde(String key, MapItemEntity entity);
	
	public T mapRow(Map<String, Object> rs);
}
