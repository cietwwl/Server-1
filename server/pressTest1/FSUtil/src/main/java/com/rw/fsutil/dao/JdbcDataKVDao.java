package com.rw.fsutil.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * @author allen
 * @version 1.0
 */
public class JdbcDataKVDao<T> {

	private JdbcTemplate template = null;

	private ClassInfo classInfoPojo = null;

	/*
	 * public JdbcDataMTDao(Class<T> clazz){ this(clazz, "dataSourceMT"); }
	 */

	public JdbcDataKVDao(Class<T> clazz, String dsName) {
		DruidDataSource dataSource = SpringContextUtil.getBean(dsName);
		template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);

		try {
			classInfoPojo = new ClassInfo(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean saveOrUpdate(String key, String value) {
		StringBuilder sql = new StringBuilder("");

		sql.append("REPLACE into ").append(classInfoPojo.getTableName()).append(" (dbkey, dbvalue) values(?,?)");

		// this.template.update(sql.toString(),new Object[]{key, HexUtil.bytes2HexStr(value.getBytes("UTF-8"))});
		int affectedRows = this.template.update(sql.toString(), new Object[] { key, value });
		//lida 2015-09-23 执行成功返回的结果是2
		return affectedRows > 0;
	}

	public String get(String key) {

		String sql = "select dbvalue from " + classInfoPojo.getTableName() + " where dbkey=?";
		String value = null;
		List<String> result = template.queryForList(sql, String.class, key);
		if (!StringUtils.isEmpty(result) && result.size() > 0) {
			// value = new String(HexUtil.hexStr2Bytes(result.get(0)), "UTF-8");
			value = new String(result.get(0));
		}
		return value;
	}

	private List<String> findBySql(String sql) {
		return template.queryForList(sql, String.class);

	}

	public List<String> getAll() {

		String sql = "select dbvalue from " + classInfoPojo.getTableName();

		return findBySql(sql);
	}

	public void delete(String key) {
		// 拼装sql
		String sql = "delete from " + classInfoPojo.getTableName() + " where dbkey=?";
		template.update(sql, key);
	}

}