package com.rw.fsutil.cacheDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Statement;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * 
 * 依赖数据库生成的主键 
 * 构造传入的table的主键必须设置成自增长
 * @author Jamaz
 *
 */
public class IdentityIdGenerator {

	private final JdbcTemplate template;

	private final String sql;

	public IdentityIdGenerator(String tableName, DruidDataSource dataSource) {
		this.template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		this.sql = "insert into " + tableName + " values()";
		if(!validateIsAutoIncrement(tableName)){
			throw new ExceptionInInitializerError("该表不支持自增长主键："+tableName);
		}
	}

	public IdentityIdGenerator(String tableName, String dsName) {
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		this.template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		this.sql = "insert into " + tableName + " values()";
		if(!validateIsAutoIncrement(tableName)){
			throw new ExceptionInInitializerError("该表不支持自增长主键："+tableName);
		}
	}

	public long generateId() {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	private boolean validateIsAutoIncrement(String tableName) {
		String sql = "select * from " + tableName + " limit 0";
		return template.query(sql, new ResultSetExtractor<Boolean>(){

			@Override
			public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData metaData = rs.getMetaData();
				int count = metaData.getColumnCount();
				for(int i = 1;i <= count;i++){
					if(metaData.isAutoIncrement(i)){
						return Boolean.TRUE;
					}
				}
				return Boolean.FALSE;
			}});
	}

	public static void main(String[] args) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setName("driverClassName");
		String url = "jdbc:mysql://192.168.4.250:3306/gods_rank?useUnicode=true&amp;characterEncoding=utf8&characterResultSets=utf8";
		String driverClass = "com.mysql.jdbc.Driver";
		dataSource.setDriverClassName(driverClass);
		String userName = "root";
		String password = "123456";
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setMaxActive(100);
		dataSource.setMaxWait(60000);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(true);
		dataSource.setTestOnReturn(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setTimeBetweenEvictionRunsMillis(300000);
		try {
			dataSource.init();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
		IdentityIdGenerator generator = new IdentityIdGenerator("testid", dataSource);
		System.out.println(generator.generateId());
		System.out.println(generator.generateId());
	}
}
