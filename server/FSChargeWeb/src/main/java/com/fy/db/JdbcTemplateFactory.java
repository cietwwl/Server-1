package com.fy.db;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

public class JdbcTemplateFactory {
	
	public static JdbcTemplate buildJdbcTemplate(String urlP, String usernameP, String passwordP, int maxActiveP) throws SQLException {
		return buildJdbcTemplate(newDataSource(urlP, usernameP, passwordP, maxActiveP));
	}
	public static JdbcTemplate buildJdbcTemplate(DruidDataSource dataSource) {
		JdbcTemplate jdbcTemplateP = new JdbcTemplate();
		jdbcTemplateP.setDataSource(dataSource);
		jdbcTemplateP.afterPropertiesSet();
		return jdbcTemplateP;
	}


	public static DruidDataSource newDataSource(String urlP, String usernameP, String passwordP, int maxActiveP) throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		String driverClass="com.mysql.jdbc.Driver";

		int maxActive=maxActiveP;
		long maxWaitMillis = 60000;
		String validationQuery = "SELECT 1";
		boolean testOnBorrow = true;
		boolean testOnReturn = true;
		boolean testWhileIdle = true;
		long timeBetweenEvictionRunsMillis = 300000;
		
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(urlP);
		dataSource.setUsername(usernameP);
		dataSource.setPassword(passwordP);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWaitMillis);
		dataSource.setValidationQuery(validationQuery);

		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		
		dataSource.init();
		return dataSource;
	}
	
	

}
