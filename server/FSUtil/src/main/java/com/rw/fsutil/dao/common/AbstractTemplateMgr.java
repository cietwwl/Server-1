package com.rw.fsutil.dao.common;

import org.springframework.jdbc.core.JdbcTemplate;

public interface AbstractTemplateMgr {
	
	public JdbcTemplate getInstance();
	
}
