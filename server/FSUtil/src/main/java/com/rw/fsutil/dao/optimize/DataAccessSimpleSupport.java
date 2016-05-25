package com.rw.fsutil.dao.optimize;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.util.SpringContextUtil;

public class DataAccessSimpleSupport {

	public DataAccessSimpleSupport() {
	}

	public JdbcTemplate getMainTemplate() {
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		return JdbcTemplateFactory.buildJdbcTemplate(dataSource);
	}

	public int getTableIndex(String userId, int tableCount) {
		boolean isNumber = true;
		int len = userId.length();
		for (int i = 0; i < len; i++) {
			char c = userId.charAt(i);
			if (!Character.isDigit(c)) {
				isNumber = false;
				break;
			}
		}
		int tableIndex;
		if (isNumber) {
			Long id = Long.parseLong(userId);
			tableIndex = (int) (id % tableCount);
		} else {
			tableIndex = Math.abs(userId.hashCode() % tableCount);
		}
		return tableIndex;
	}

}
