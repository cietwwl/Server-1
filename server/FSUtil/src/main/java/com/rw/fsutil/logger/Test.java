package com.rw.fsutil.logger;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setName("driverClassName");
		String url = "jdbc:mysql://localhost:3306/fs_data_mt?useUnicode=true&amp;characterEncoding=utf8&characterResultSets=utf8";
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
		LoggerQueue queue = new LoggerQueue("logger_record", "192.168.2.253", 10001, 2000, dataSource);
		for (int i = 0; i < 50; i++) {
			queue.addLogger("发送数据：" + i);
			Thread.sleep(10);
		}
	}
}
