package com.rw.fsutil.dao.optimize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * <pre>
 * 数据库访问的支持方法
 * 此类全部是静态方法，只能用于服务器启动阶段
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class DataAccessStaticSupport {

	private static String dataKVName = "table_kvdata";
	
	public static List<String> getDataKVTableNameList(JdbcTemplate template){
		return getTableNameList(template, dataKVName);
	}
	
	/**
	 * 根据名字获取表名分区
	 * @param template
	 * @param tableName
	 * @return
	 */
	public static List<String> getTableNameList(JdbcTemplate template, String tableName) {
		//这里需要做版本验证
		ArrayList<String> list = new ArrayList<String>();
		String defaultSql = "select 1 from "+tableName;
		try {
			template.query(defaultSql, rowCall);
			list.add(tableName);
			return list;
		} catch (Exception e) {
		}
		List<String> likeList = template.queryForList("show tables like '" + tableName + "%'", String.class);
		int size = likeList.size();
		if (size == 0) {
			throw new ExceptionInInitializerError("不存在该表：" + tableName);
		}
		int len = tableName.length();
		TreeMap<Integer, String> set = new TreeMap<Integer, String>();
		for (int i = 0; i < size; i++) {
			String name = likeList.get(i);
			String number = name.substring(len, name.length());
			try {
				int num = Integer.parseInt(number);
				if (num < 10) {
					if (!number.equals("0" + num)) {
						continue;
					}
				} else if (!String.valueOf(num).equals(number)) {
					continue;
				}
				if (set.put(num, name) != null) {
					throw new ExceptionInInitializerError("数据库分表结构异常：" + tableName + ",异常表名：" + name);
				}
			} catch (Exception e) {
				continue;
			}
		}
		int first = set.firstKey();
		if (first != 0) {
			throw new ExceptionInInitializerError("数据库分表结构异常：" + tableName + ",fisrt table name:" + set.firstEntry().getValue());
		}
		int last = set.lastKey();
		if (last != (set.size() - 1)) {
			throw new ExceptionInInitializerError("数据库分表结构异常：" + tableName + ",last table name:" + set.lastEntry().getValue());
		}
		list.addAll(set.values());
		return list;
	}

	static RowCallbackHandler rowCall = new RowCallbackHandler() {

		@Override
		public void processRow(ResultSet rs) throws SQLException {

		}
	};
}
