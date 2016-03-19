package com.rw.fsutil.dao.common;

import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;



/**
 * @author allen
 * @version 1.0
 */
public class RawSqlJdbc {
	
	private JdbcTemplate template;
	
	public RawSqlJdbc(DruidDataSource dataSource){
		template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
	}
	
	public <T> List<T> findBySql(String sql,Object[] args, ClassInfo classInfoPojo, Class<T> clazz) {
		
		List<T> resultList = template.query(sql, args, new CommonRowMapper<T>(classInfoPojo));
		
		return resultList;
	}
	

	public static void main(String[] args) throws SQLException {
		
//		String url = "jdbc:mysql://192.168.2.230:3306/gods_cfg_{zoneId}?useUnicode=true&amp;characterEncoding=utf8&amp;characterResultSets=utf8";
//		String username = "root";
//		String password = "123456";
//		int maxActive = 1000;
//		DruidDataSource dataSource = JdbcTemplateFactory.newDataSource(url, username, password, maxActive );		
//		JdbcTemplate jdbcTemplateTmp = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
//		
		
	}

	
}