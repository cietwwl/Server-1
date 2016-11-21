package com.playerdata.charge.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Column;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class ChargeRecordDAO {

	private static ChargeRecordDAO _instance = new ChargeRecordDAO(true);
	
	public static ChargeRecordDAO getInstance() {
		return _instance;
	}
	
	private JdbcTemplate _jdbcTemplate;
	private ClassInfo _classInfo;
	private String _querySql; // 查询的语句
	private String _insertSql; // 插入的语句
	
	protected ChargeRecordDAO(boolean setJdbcTemplate) {
		if (setJdbcTemplate) {
			_jdbcTemplate = DataAccessFactory.getSimpleSupport().getMainTemplate();
		}
		_classInfo = new ClassInfo(ChargeRecord.class);
		String mainFieldName = _classInfo.getIdField().getAnnotation(Column.class).name();
		_querySql = "select count(*) from " + _classInfo.getTableName() + " where " + mainFieldName + " = '%s'";
		StringBuilder insertFields = new StringBuilder();
		StringBuilder insertHolds = new StringBuilder();
		try {
			_classInfo.extractColumn(insertFields, insertHolds, new StringBuilder());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		_insertSql = "insert into " + _classInfo.getTableName() + "(" + insertFields + ") values(" + insertHolds + ")";
	}
	
	public boolean isRecordExists(String tradeNo) {
		if (tradeNo == null || (tradeNo = tradeNo.trim()).length() == 0) {
			throw new IllegalArgumentException("非法的tradeNo：" + tradeNo);
		}
		Integer result = _jdbcTemplate.queryForObject(String.format(_querySql, tradeNo), Integer.class);
		return result != null && result.intValue() > 0;
	}

	public boolean addChargeRecord(ChargeRecord record) {
		int result = _jdbcTemplate.update(_insertSql, new ChargeRecordPreparedStatementSetter(record, _classInfo));
		return result > 0;
	}
	
	private static class ChargeRecordPreparedStatementSetter implements PreparedStatementSetter {

		private ChargeRecord _target;
		private ClassInfo _classInfo;
		
		ChargeRecordPreparedStatementSetter(ChargeRecord pTarget, ClassInfo pClassInfo) {
			this._target = pTarget;
			this._classInfo = pClassInfo;
		}
		
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			try {
				List<Object> list = _classInfo.extractInsertAttributes(_target);
				for (int i = 0, size = list.size(); i < size; i++) {
					ps.setObject(i + 1, list.get(i));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	public static void main(String[] args) throws Exception {
		ChargeRecordDAO dao = new ChargeRecordDAO(false);
		dao._jdbcTemplate = com.rw.fsutil.dao.common.JdbcTemplateFactory.buildJdbcTemplate("jdbc:mysql://localhost:3306/fs_data_mt?rewriteBatchedStatements=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;characterResultSets=utf8", "root", "123456", 24);
		ChargeRecord record = new ChargeRecord();
		record.setUserId("11111");
		record.setSdkUserId("222222");
		record.setTradeNo("tradeNo");
		record.setMoney(600);
		record.setCurrencyType("CNY");
		record.setChannelId("1001");
		record.setItemId("com.fs.item6");
		record.setChargeTime(System.currentTimeMillis());
		dao.addChargeRecord(record);
		System.out.println(dao.isRecordExists("tradeNo"));
	}
}
