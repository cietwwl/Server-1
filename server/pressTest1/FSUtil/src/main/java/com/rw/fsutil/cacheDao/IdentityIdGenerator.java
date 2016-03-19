package com.rw.fsutil.cacheDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Statement;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.json.JSONArray;
import com.rw.fsutil.json.JSONObject;
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

	public static void main(String[] args) throws Exception {
		test3();
	}
	
	public static void test2(){
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
	
	public static void test3() throws Exception{
	      DruidDataSource dataSource = new DruidDataSource();
	      dataSource.setName("driverClassName");
	      dataSource.setUrl("jdbc:mysql://localhost:3306/fs_data_mt?useUnicode=true&amp;characterEncoding=utf8&characterResultSets=utf8");
	      dataSource.setUsername("root");
	      dataSource.setPassword("123456");
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
	      }
	      JdbcTemplate template = new JdbcTemplate();
	      template.setDataSource(dataSource);
	      
	      final String sql = "update mt_user_hero set dbvalue = ? where dbkey = ?";
	      
	      JSONObject json = new JSONObject();
	      json.put("userId", "c934fe1f-4f5f-4d37-a534-09f80d0793e4");
	      JSONArray array = new JSONArray();
	      array.put("c934fe1f-4f5f-4d37-a534-09f80d0793e4");
	      array.put("f0f8b842-9349-4934-ab07-ab44e4447109");
	      array.put("0f99ea6f-9cf0-4911-be6d-bd794e143159");
	      array.put("0a6759c8-5af5-4a77-83c6-b51316568eea");
	      array.put("153e6555-8538-44de-b9a0-7e1e963b3826");
	      json.put("heroIds", array.toString());
	      
	      
	      int affectedRows = template.update(sql.toString(), new Object[] {"1f-4f5f-4d37-a534-09f80d0793e4", json.toString()});
	      System.out.println(affectedRows);
//	      
//	      KeyHolder keyHolder = new GeneratedKeyHolder();		
//			template.update(new PreparedStatementCreator() {
//		        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//		            PreparedStatement ps = con.prepareStatement(sql ,
//		                new String[] {primaryKey});
//		            int index = 0;
//		            for (Object param : list) {
//		                index++;
//		                ps.setObject(index, param);
//		            }
//		            return ps;
//		        }
//		    }, keyHolder);
	}
	
	public static void test(){
      DruidDataSource dataSource = new DruidDataSource();
      dataSource.setName("driverClassName");
      dataSource.setUrl("jdbc:mysql://localhost:3306/fs_data_mt?useUnicode=true&amp;characterEncoding=utf8&characterResultSets=utf8");
      dataSource.setUsername("root");
      dataSource.setPassword("123456");
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
      }
      JdbcTemplate template = new JdbcTemplate();
      template.setDataSource(dataSource);
      
      final String sql = "REPLACE into user (level,kickOffCoolTime,sex,headImage,userName,zoneRegInfo,userId,lastLoginTime,createTime,zoneId,vip,exp,account) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      
      final String primaryKey = "userId";
      
      //[1, 0, 1, 10001, 拓跋天赋, null, 100100027320, 0, 1452345142745, 3, 0, 0, 0109962055]
      final ArrayList<Object> list = new ArrayList<Object>();
      list.add(1);
      list.add(0L);
      list.add(1);
      list.add("10001");
      list.add("拓跋天赋");
      list.add(null);
      list.add("100100027320");
      list.add(0L);
      list.add(0L);
      list.add(3);
      list.add(0);
      list.add(0L);
      list.add("0109962055");
      
      KeyHolder keyHolder = new GeneratedKeyHolder();		
		template.update(new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	            PreparedStatement ps = con.prepareStatement(sql ,
	                new String[] {primaryKey});
	            int index = 0;
	            for (Object param : list) {
	                index++;
	                ps.setObject(index, param);
	            }
	            return ps;
	        }
	    }, keyHolder);
	}
	
}
