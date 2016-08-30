package com.rwbase.dao.majorDatas;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class MajorDataCacheFactory {

	private static MajorDataCache cache;

	static {
		JdbcTemplate template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		if (template == null) {
			throw new ExceptionInInitializerError("template is null");
		}
		cache = new MajorDataCache(template);
	}

	public static MajorDataCache getCache() {
		return cache;
	}
}
